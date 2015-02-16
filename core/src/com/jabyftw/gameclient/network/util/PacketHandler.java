package com.jabyftw.gameclient.network.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.*;
import com.jabyftw.gameclient.event.EventHandler;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.event.events.packethandler.*;
import com.jabyftw.gameclient.event.util.AbstractListenerHandler;
import com.jabyftw.gameclient.event.util.ListenerFilter;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rafael on 06/02/2015.
 */
public abstract class PacketHandler extends AbstractListenerHandler implements Runnable, Tickable, Disposable {

    private static final Vector2 trafficBytes = new Vector2(); // Sent, received

    private final Map<Listener.PacketListener.DeliverType, Stack<Packet>> packetEventQueue = Collections.synchronizedMap(new LinkedHashMap<Listener.PacketListener.DeliverType, Stack<Packet>>());
    private final Stack<Packet> packetQueue = new Stack<Packet>();
    protected final EventHandler eventHandler;

    private final Type handlerType;
    private final Socket connection;

    private DataOutputStream outputStream;
    private BufferedReader inputStream;

    private boolean closeThread = false;

    protected PacketHandler(Type handlerType, EventHandler eventHandler, Socket connection) {
        if(connection == null || !connection.isConnected())
            throw new IllegalArgumentException("Connection can't be null or invalid!");

        this.eventHandler = eventHandler;

        this.handlerType = handlerType;
        this.connection = connection;

        this.outputStream = new DataOutputStream(connection.getOutputStream());
        this.inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        this.packetEventQueue.put(Listener.PacketListener.DeliverType.RECEIVED, new Stack<Packet>());
        this.packetEventQueue.put(Listener.PacketListener.DeliverType.SENT, new Stack<Packet>());
    }

    public Socket getConnection() {
        return connection;
    }

    public static float getBytesSent() {
        return trafficBytes.x;
    }

    public static float getBytesReceived() {
        return trafficBytes.y;
    }

    @Override
    public void run() {
        while(!closeThread) {
            long start = System.nanoTime();
            { // Send packets
                while(!packetQueue.empty()) {
                    sendPacket(packetQueue.remove(0));
                }
            }
            { // Received packets
                try {
                    while(inputStream.ready()) {
                        receivePacket(inputStream.readLine());
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            long timeOnTick = System.nanoTime() - start;
            { // Wait for thread
                long maximumWaitTime = (long) (Constants.Gameplay.PACKET_HANDLER_STEP * (float) TimeUnit.SECONDS.toNanos(1));
                long waitTime = maximumWaitTime - timeOnTick;

                if(waitTime > 0)
                    synchronized(this) {
                        try {
                            int waitTimeNanos = (int) (waitTime % TimeUnit.MILLISECONDS.toNanos(1));
                            long waitTimeMillis = (long) ((waitTime - waitTimeNanos) / (float) TimeUnit.MILLISECONDS.toNanos(1));

                            wait(waitTimeMillis, waitTimeNanos);
                        } catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
        eventHandler.callEvent(new PostCloseConnectionEvent());
        dispose();
    }

    @Override
    public void update(float deltaTime) {
        synchronized(packetEventQueue) {
            Stack<Packet> packetsReceived = packetEventQueue.get(Listener.PacketListener.DeliverType.RECEIVED);
            {
                while(!packetsReceived.empty()) {
                    handleListener(Listener.PacketListener.DeliverType.RECEIVED, packetsReceived.remove(0));
                }
            }
            Stack<Packet> packetsSent = packetEventQueue.get(Listener.PacketListener.DeliverType.SENT);
            {
                while(!packetsSent.empty()) {
                    handleListener(Listener.PacketListener.DeliverType.SENT, packetsSent.remove(0));
                }
            }
        }
    }

    private void receivePacket(String receivedLine) {
        try {
            int length = receivedLine.getBytes().length;
            if(Constants.isEncryptingPackets)
                receivedLine = Base64Coder.decodeString(receivedLine);

            JsonValue jsonValue = new JsonReader().parse(receivedLine);

            PacketType type = PacketType.valueOf(jsonValue.getInt("deliverType"));
            Packet packet = new Json().fromJson(type.getPacketClass(), receivedLine); // packet deliverType's class casted to Packet.class

            { // Control traffic
                trafficBytes.add(0, length);
                if(Constants.isDebuggingNetwork)
                    System.out.println("Received " + length + " bytes of " + packet.getPacketType().name() + " packet from " + connection.getRemoteAddress()
                            + " (total: " + Util.formatDecimal((trafficBytes.y / 1000f), 2) + "kb)");
            }

            ReceivePacketEvent packetEvent = new ReceivePacketEvent(packet);
            eventHandler.callEvent(packetEvent);
            if(packetEvent.isCancelled()) return;

            synchronized(packetEventQueue) {
                packetEventQueue.get(Listener.PacketListener.DeliverType.RECEIVED).push(packet);
            }
        } catch(Throwable e) {
            //e.printStackTrace();
            eventHandler.callEvent(new ReceivePacketErrorEvent(e));
        }
    }

    private void sendPacket(Packet packet) {
        try {
            SendPacketEvent packetEvent = new SendPacketEvent(packet);
            eventHandler.callEvent(packetEvent);
            if(packetEvent.isCancelled()) return;

            synchronized(packetEventQueue) {
                packetEventQueue.get(Listener.PacketListener.DeliverType.SENT).push(packet);
            }

            String jsonPacket = new Json(JsonWriter.OutputType.minimal).toJson(packet);
            if(Constants.isEncryptingPackets)
                jsonPacket = Base64Coder.encodeString(jsonPacket);

            jsonPacket += "\n";
            byte[] bytes = jsonPacket.getBytes();

            outputStream.write(bytes);
            outputStream.flush();

            { // Control traffic
                trafficBytes.add(bytes.length, 0);
                if(Constants.isDebuggingNetwork)
                    System.out.println("Sent " + bytes.length + " bytes of " + packet.getPacketType().name() + " packet to " + connection.getRemoteAddress()
                            + " (total: " + Util.formatDecimal((trafficBytes.x / 1000f), 2) + "kb)");
            }
        } catch(Throwable e) {
            //e.printStackTrace();
            eventHandler.callEvent(new SendPacketErrorEvent(e));
        }
    }

    @Override
    public void dispose() {
        try {
            inputStream.close();
            outputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        connection.dispose();
    }

    public void addPacketToQueue(Packet packet) {
        if(packet == null)
            throw new NullPointerException("Null packet being queued!");
        if(!packet.getPacketType().canSendPacket(handlerType))
            throw new IllegalArgumentException("Invalid packet being queued!");
        if(packet.getPacketType() == PacketType.KILL_CONNECTION)
            throw new IllegalArgumentException(PacketType.KILL_CONNECTION.name() + " packet being queued!");

        AddPacketToQueueEvent event = new AddPacketToQueueEvent(packet);
        eventHandler.callEvent(event);
        if(event.isCancelled()) return;

        packetQueue.push(packet);
    }

    public void closeThread(PacketKillConnection.Reason reason) {
        eventHandler.callEvent(new PreCloseConnectionEvent());
        if(reason != null)
            sendPacket(new PacketKillConnection(reason));
        this.closeThread = true;
    }

    private void handleListener(final Listener.PacketListener.DeliverType type, final Packet packet) {
        Util.handleAnnotationEventSystem(new ListenerFilter() {
            @Override
            public Class<? extends Annotation> getAnnotationClass() {
                return Listener.PacketListener.class;
            }

            @Override
            public Object[] getMethodArguments() {
                return new Object[]{packet};
            }

            @Override
            public boolean filterAnnotation(Method method, Annotation annotation) {
                if(((Listener.PacketListener) annotation).deliverType() == type) {
                    for(Parameter parameter : method.getParameters()) {
                        if(parameter.getType().isAssignableFrom(packet.getClass()))
                            return true;
                    }
                }
                return false;
            }
        }, listenerArray);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof PacketHandler && ((PacketHandler) obj).getConnection().getRemoteAddress().equalsIgnoreCase(connection.getRemoteAddress());
    }

    public enum Type {
        SERVER,
        CLIENT
    }
}
