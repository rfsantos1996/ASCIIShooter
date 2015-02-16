package com.jabyftw.gameclient.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.event.events.packethandler.ReceivePacketErrorEvent;
import com.jabyftw.gameclient.event.events.packethandler.ReceivePacketEvent;
import com.jabyftw.gameclient.event.events.packethandler.SendPacketErrorEvent;
import com.jabyftw.gameclient.event.events.packethandler.SendPacketEvent;
import com.jabyftw.gameclient.gamestates.startup.DisconnectedFromServerState;
import com.jabyftw.gameclient.network.packets.PacketKeepAwake;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.network.util.PacketHandler;
import com.jabyftw.gameclient.util.Constants;

/**
 * Created by Rafael on 13/01/2015.
 */
public class ClientPacketHandler extends PacketHandler implements Listener {

    private float timeSinceLastPacketSent = 0;

    public ClientPacketHandler() throws GdxRuntimeException {
        super(PacketHandler.Type.CLIENT, Main.getEventHandler(), Gdx.net.newClientSocket(Net.Protocol.TCP, Constants.Multiplayer.CONNECTION_HOST, Constants.Multiplayer.CONNECTION_PORT, null));
        eventHandler.addListener(this);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        timeSinceLastPacketSent += deltaTime;
        if(timeSinceLastPacketSent > Constants.Multiplayer.TIME_FOR_CLIENT_KEEP_AWAKE_PACKET) {
            addPacketToQueue(new PacketKeepAwake());
            timeSinceLastPacketSent = 0;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        eventHandler.removeListener(this);
    }

    @EventListener(ignoreCancelled = true)
    public void onPacketReceive(ReceivePacketEvent event) {
        if(event.getPacket().getPacketType() == PacketType.KILL_CONNECTION)
            Main.setCurrentGameState(new DisconnectedFromServerState(((PacketKillConnection) event.getPacket()).getReason().getDisconnectMessage()));
    }

    @EventListener(ignoreCancelled = true)
    public void onPacketSend(SendPacketEvent event) {
        timeSinceLastPacketSent = 0;
    }

    @Listener.EventListener
    public void onPacketReceivingError(ReceivePacketErrorEvent event) {
        closeThread(PacketKillConnection.Reason.CLIENT_LOST_CONNECTION);
        event.getThrowable().printStackTrace();
        System.out.println("Failed to receive packet: " + event.getThrowable().getMessage());
    }

    @Listener.EventListener
    public void onPacketSendingError(SendPacketErrorEvent event) {
        closeThread(null);
        event.getThrowable().printStackTrace();
        System.out.println("Failed to send packet: " + event.getThrowable().getMessage());
    }
}
