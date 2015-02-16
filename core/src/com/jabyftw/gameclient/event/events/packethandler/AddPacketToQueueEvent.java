package com.jabyftw.gameclient.event.events.packethandler;

import com.jabyftw.gameclient.event.EventType;
import com.jabyftw.gameclient.event.util.AbstractCancellableEvent;
import com.jabyftw.gameclient.network.util.Packet;

/**
 * Created by Rafael on 06/02/2015.
 */
public class AddPacketToQueueEvent extends AbstractCancellableEvent {

    private final Packet packet;

    public AddPacketToQueueEvent(Packet packet) {
        super(EventType.ADD_PACKET_TO_QUEUE_EVENT);
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }
}
