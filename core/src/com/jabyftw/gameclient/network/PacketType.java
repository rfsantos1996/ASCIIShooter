package com.jabyftw.gameclient.network;

import com.jabyftw.gameclient.network.packets.PacketKeepAwake;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.network.packets.client.PacketLoginRequest;
import com.jabyftw.gameclient.network.packets.client.PacketPingRequest;
import com.jabyftw.gameclient.network.packets.client.PacketRegisterRequest;
import com.jabyftw.gameclient.network.packets.server.PacketRegisterResponse;
import com.jabyftw.gameclient.network.packets.server.PacketLoginResponse;
import com.jabyftw.gameclient.network.packets.server.PacketPingResponse;

/**
 * Created by Rafael on 12/01/2015.
 */
public enum PacketType {

    /*
     * SERVER
     */
    PING_RESPONSE(Sender.SERVER, PacketPingResponse.class),
    LOGIN_RESPONSE(Sender.SERVER, PacketLoginResponse.class),
    REGISTER_RESPONSE(Sender.SERVER, PacketRegisterResponse.class),

    /*
     * CLIENT
     */
    PING_REQUEST(Sender.CLIENT, PacketPingRequest.class),
    LOGIN_REQUEST(Sender.CLIENT, PacketLoginRequest.class),
    REGISTER_REQUEST(Sender.CLIENT, PacketRegisterRequest.class),

    /*
     * BOTH
     */
    KEEP_AWAKE(Sender.BOTH, PacketKeepAwake.class),
    KILL_CONNECTION(Sender.BOTH, PacketKillConnection.class);

    private final Sender sender;
    private final Class packetClass;

    private PacketType(Sender sender, Class packetClass) {
        this.sender = sender;
        this.packetClass = packetClass;
    }

    public Sender getSender() {
        return sender;
    }

    public Class getPacketClass() {
        return packetClass;
    }

    public static PacketType valueOf(int ordinal) {
        return values()[ordinal];
    }

    public int getId() {
        return ordinal();
    }

    public enum Sender {

        SERVER,
        CLIENT,
        BOTH

    }
}
