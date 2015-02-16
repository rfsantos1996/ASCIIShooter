package com.jabyftw.gameclient.network.listeners;

import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.network.ClientPacketHandler;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.network.util.Packet;

/**
 * Created by Rafael on 07/02/2015.
 */
public abstract class AbstractPacketListener implements Listener {

    final Array<PacketType> validPacketTypes = new Array<PacketType>();
    private final ClientPacketHandler packetHandler;
    private final boolean checkPacketTypes;

    public AbstractPacketListener(ClientPacketHandler packetHandler) {
        this(packetHandler, false);
    }

    AbstractPacketListener(ClientPacketHandler packetHandler, boolean checkPacketTypes) {
        this.packetHandler = packetHandler;
        this.checkPacketTypes = checkPacketTypes;
    }

    @Listener.PacketListener(deliverType = Listener.PacketListener.DeliverType.RECEIVED)
    public void onPacketReceive(Packet packet) {
        if(checkPacketTypes && !validPacketTypes.contains(packet.getPacketType(), true) && packet.getPacketType().canBeIgnored()) // isn't desired AND can be ignored
            packetHandler.closeThread(PacketKillConnection.Reason.CLIENT_BAD_PACKET_RECEIVED);
    }
}
