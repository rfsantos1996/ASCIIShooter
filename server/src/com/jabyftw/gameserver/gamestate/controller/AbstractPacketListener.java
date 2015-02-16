package com.jabyftw.gameserver.gamestate.controller;

import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameserver.network.ServerPacketHandler;

/**
 * Created by Rafael on 06/02/2015.
 */
public abstract class AbstractPacketListener implements Listener {

    protected final Array<PacketType> validPacketTypes = new Array<PacketType>();
    protected final ServerPacketHandler packetHandler;

    public AbstractPacketListener(ServerPacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    @PacketListener(deliverType = PacketListener.DeliverType.RECEIVED)
    public void onPacketReceive(Packet packet) {
        if(!validPacketTypes.contains(packet.getPacketType(), true) && packet.getPacketType().canBeIgnored()) // isn't desired AND can be ignored
            packetHandler.closeThread(PacketKillConnection.Reason.SERVER_BAD_PACKET_RECEIVED);
    }
}
