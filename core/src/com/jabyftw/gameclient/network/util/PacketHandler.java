package com.jabyftw.gameclient.network.util;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.*;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.PacketKeepAwake;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.util.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rafael on 12/01/2015.
 */
public abstract class PacketHandler implements Runnable, Disposable {

    public static final int CONNECTION_PORT = 5450;
    public static final long TIME_TO_SEND_KEEP_AWAKE = TimeUnit.SECONDS.toMillis(2);
    public static final long TIMEOUT_UNTIL_DISCONNECT = TimeUnit.SECONDS.toMillis(5);

    private final Stack<Packet> packets = new Stack<Packet>();

    private Socket connection;
    protected DataOutputStream outputStream;
    protected BufferedReader inputStream;

    private long receivedBytes = 0;
    private long bytesSent = 0;

    private boolean closeThread = false;
    private long timeOfTheLastPacketSent = System.currentTimeMillis();
    private long timeOfTheLastPacketReceived = System.currentTimeMillis();

    public PacketHandler(Socket connection) {
        if(connection == null || !connection.isConnected())
            throw new IllegalArgumentException("Connection can't be null!");

        this.connection = connection;
        this.outputStream = new DataOutputStream(this.connection.getOutputStream());
        this.inputStream = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
    }

    @Override
    public void run() {
        while(!shouldCloseThread()) {
            long start = System.currentTimeMillis();

            sendKeepAwakePacket();
            {
                // Send packets
                while(!packets.empty()) {
                    sendPacketImmediate(packets.pop());
                }
            }

            {
                // Receive packets if ready
                try {
                    while(inputStream.ready()) {
                        //String lineFromServer = inputStream.readLine();
                        String lineFromServer = inputStream.readLine();

                        receivedBytes += lineFromServer.getBytes().length;

                        lineFromServer = Base64Coder.decodeString(lineFromServer);

                        JsonReader jsonReader = new JsonReader();
                        JsonValue jsonValue = jsonReader.parse(lineFromServer);

                        PacketType type = PacketType.valueOf(jsonValue.getInt("type"));
                        Packet packet = (Packet) new Json().fromJson(type.getPacketClass(), lineFromServer);

                        System.out.println("Received " + packet.getPacketType().name() + " packet from SERVER (total: " + Util.formatDecimal(((float) receivedBytes / 1000f), 2) + "kb)");
                        this.timeOfTheLastPacketReceived = System.currentTimeMillis();

                        // TODO per server and client keep awake packets (client sends, server responds) -> define "TYPE" -> client/server on PacketHandler (here)
                        if(packet.getPacketType() == PacketType.KILL_CONNECTION) {
                            dispose();
                            return;
                        } else {
                            receivePacket(packet);
                        }

                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            long timeToPause = (long) (Main.STEP * (float) TimeUnit.SECONDS.toMillis(1)) - (System.currentTimeMillis() - start);
            if(timeToPause > 0)
                try {
                    synchronized(this) {
                        wait(timeToPause);
                    }
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
        }
        closeThread(true);
    }

    private void sendKeepAwakePacket() {
        long timeWithoutReceiving = System.currentTimeMillis() - timeOfTheLastPacketReceived;
        long timeWithoutSending = (System.currentTimeMillis() - timeOfTheLastPacketSent);

        if((timeWithoutReceiving > TIME_TO_SEND_KEEP_AWAKE) && timeWithoutSending > TIME_TO_SEND_KEEP_AWAKE)
            sendPacket(new PacketKeepAwake());
    }

    private boolean shouldCloseThread() {
        return closeThread || (System.currentTimeMillis() - timeOfTheLastPacketReceived) > TIMEOUT_UNTIL_DISCONNECT;
    }

    @Override
    public void dispose() {
        try {
            outputStream.close();
            inputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        connection.dispose();
        System.out.println("Connection closed );");
    }

    public void closeThread(boolean force) {
        if(force) {
            sendPacketImmediate(new PacketKillConnection());
            dispose();
        } else {
            this.closeThread = true;
        }
    }

    public Socket getConnection() {
        return connection;
    }

    public void sendPacket(Packet packet) {
        checkPacketState(packet);
        packets.push(packet);
    }

    public void sendPacketImmediate(Packet packet) {
        checkPacketState(packet);
        try {
            String jsonPacket = Base64Coder.encodeString(new Json(JsonWriter.OutputType.minimal).toJson(packet));

            byte[] bytes = (jsonPacket + "\n").getBytes();
            bytesSent += bytes.length;

            outputStream.write(bytes);
            outputStream.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
        timeOfTheLastPacketSent = System.currentTimeMillis();
        System.out.println("Sent " + packet.getPacketType().name() + " packet to SERVER (total: " + Util.formatDecimal(((float) bytesSent / 1000f), 2) + "kb)");
    }

    private void checkPacketState(Packet packet) {
        if(packet == null)
            throw new IllegalArgumentException("Packet CAN'T be null!");
    }

    public abstract void receivePacket(Packet packet);
}
