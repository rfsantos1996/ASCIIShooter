package com.jabyftw.gameclient.network;

import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.network.packets.PacketKeepAwake;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.network.packets.client.PacketLoginRequest;
import com.jabyftw.gameclient.network.packets.client.PacketRegisterRequest;
import com.jabyftw.gameclient.network.packets.client.PacketValidateLayoutsRequest;
import com.jabyftw.gameclient.network.packets.server.PacketLoginResponse;
import com.jabyftw.gameclient.network.packets.server.PacketRegisterResponse;
import com.jabyftw.gameclient.network.packets.server.PacketValidateLayoutsResponse;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.network.util.PacketHandler;

/**
 * Created by Rafael on 12/01/2015.
 */
public enum PacketType {

    /*
     * SERVER
     */
    LOGIN_RESPONSE(Sender.SERVER, PacketLoginResponse.class),
    REGISTER_RESPONSE(Sender.SERVER, PacketRegisterResponse.class),
    VALIDATE_LAYOUT_RESPONSE(Sender.SERVER, PacketValidateLayoutsResponse.class),

    /*
     * CLIENT
     */
    LOGIN_REQUEST(Sender.CLIENT, PacketLoginRequest.class),
    REGISTER_REQUEST(Sender.CLIENT, PacketRegisterRequest.class),
    VALIDATE_LAYOUT_REQUEST(Sender.CLIENT, PacketValidateLayoutsRequest.class),

    /*
     * BOTH
     */
    KEEP_AWAKE(Sender.BOTH, PacketKeepAwake.class),
    KILL_CONNECTION(Sender.BOTH, PacketKillConnection.class);

    private final Sender sender;
    private final Class<? extends Packet> packetClass;

    private PacketType(Sender sender, Class<? extends Packet> packetClass) {
        this.sender = sender;
        this.packetClass = packetClass;
    }

    public Class<? extends Packet> getPacketClass() {
        return packetClass;
    }

    public static PacketType valueOf(int ordinal) {
        return values()[ordinal];
    }

    public int getId() {
        return ordinal();
    }

    public boolean canBeIgnored() {
        return this != KEEP_AWAKE && this != KILL_CONNECTION;
    }

    public boolean canSendPacket(PacketHandler.Type type) {
        return sender.isValidSender(type);
    }

    public enum Sender {

        SERVER(PacketHandler.Type.SERVER),
        CLIENT(PacketHandler.Type.CLIENT),
        BOTH(PacketHandler.Type.CLIENT, PacketHandler.Type.SERVER);

        private final Array<PacketHandler.Type> possibleSenders;

        private Sender(PacketHandler.Type... possibleSenders) {
            this.possibleSenders = new Array<PacketHandler.Type>(possibleSenders);
        }

        public boolean isValidSender(PacketHandler.Type type) {
            return possibleSenders.contains(type, true);
        }
    }
}
