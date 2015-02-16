package com.jabyftw.gameserver.network;

import com.badlogic.gdx.net.Socket;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.event.events.packethandler.PostCloseConnectionEvent;
import com.jabyftw.gameclient.event.events.packethandler.ReceivePacketErrorEvent;
import com.jabyftw.gameclient.event.events.packethandler.ReceivePacketEvent;
import com.jabyftw.gameclient.event.events.packethandler.SendPacketErrorEvent;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.network.util.LoginResponse;
import com.jabyftw.gameclient.network.util.PacketHandler;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;
import com.jabyftw.gameserver.Server;
import com.jabyftw.gameserver.gamestate.controller.BeforeLoginListener;

/**
 * Created by Rafael on 13/01/2015.
 */
public class ServerPacketHandler extends PacketHandler implements Listener {

    private OnlinePlayerProfile onlinePlayerProfile = null;
    private LoginResponse loginResponse = null;
    private String connectionName;
    private float timeSinceLastPacketReceived = 0;

    public ServerPacketHandler(Socket connection) {
        super(PacketHandler.Type.SERVER, Server.getEventHandler(), connection);
        this.connectionName = connection.getRemoteAddress();
        eventHandler.addListener(this);
        addListener(new BeforeLoginListener(this));
        System.out.println("+ Created connection to " + connectionName);
    }

    public LoginResponse getLoginResponse() {
        return loginResponse;
    }

    public void setLoginResponse(LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
    }

    public OnlinePlayerProfile getOnlinePlayerProfile() {
        return onlinePlayerProfile;
    }

    public void setOnlinePlayerProfile(OnlinePlayerProfile onlinePlayerProfile) {
        this.onlinePlayerProfile = onlinePlayerProfile;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName.toLowerCase();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        timeSinceLastPacketReceived += deltaTime;
        if(timeSinceLastPacketReceived > Constants.Multiplayer.SERVER_TIMEOUT_TIME)
            closeThread(PacketKillConnection.Reason.SERVER_YOU_TIMED_OUT);
    }

    @Override
    public void dispose() {
        super.dispose();
        eventHandler.removeListener(this);
    }

    @Listener.EventListener(ignoreCancelled = false)
    public void onReceivePacket(ReceivePacketEvent event) {
        timeSinceLastPacketReceived = 0;
    }

    @Listener.EventListener(ignoreCancelled = true)
    public void onRealReceivePacket(ReceivePacketEvent event) {
        if(event.getPacket().getPacketType() == PacketType.KILL_CONNECTION) {
            System.out.println("- Killed packet to " + ((PacketKillConnection) event.getPacket()).getReason().name());
            closeThread(null);
        }
    }

    @Listener.EventListener
    public void onPacketReceivingError(ReceivePacketErrorEvent event) {
        event.getThrowable().printStackTrace();
        System.out.println("Failed to receive packet: " + event.getThrowable().getMessage());
        closeThread(PacketKillConnection.Reason.SERVER_UNKNOWN);
    }

    @Listener.EventListener
    public void doOnPacketSendingError(SendPacketErrorEvent event) {
        event.getThrowable().printStackTrace();
        System.out.println("Failed to send packet: " + event.getThrowable().getMessage());
        closeThread(null);
    }

    @Listener.EventListener
    public void doOnClose(PostCloseConnectionEvent event) {
        Server.getInstance().removeConnectionThread(this);
        System.out.println("- Closed connection to " + connectionName);
    }
}
