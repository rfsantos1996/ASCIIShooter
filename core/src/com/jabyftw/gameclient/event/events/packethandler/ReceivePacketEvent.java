package com.jabyftw.gameclient.event.events.packethandler;

import com.jabyftw.gameclient.event.EventType;
import com.jabyftw.gameclient.event.util.AbstractCancellableEvent;
import com.jabyftw.gameclient.network.util.Packet;

/**
 * Created by Rafael on 06/02/2015.
 */
public class ReceivePacketEvent extends AbstractCancellableEvent {

    private final Packet packet;

    public ReceivePacketEvent(Packet packet) {
        super(EventType.RECEIVE_PACKET_EVENT);
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}
