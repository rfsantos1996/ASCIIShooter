package com.jabyftw.gameclient.event.events.packethandler;

import com.jabyftw.gameclient.event.EventType;
import com.jabyftw.gameclient.event.util.AbstractEvent;

/**
 * Created by Rafael on 06/02/2015.
 */
public class ReceivePacketErrorEvent extends AbstractEvent {

    private final Throwable throwable;

    public ReceivePacketErrorEvent(Throwable throwable) {
        super(EventType.RECEIVE_PACKET_ERROR_EVENT);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
