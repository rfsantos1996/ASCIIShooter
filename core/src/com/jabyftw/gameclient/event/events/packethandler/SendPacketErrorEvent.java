package com.jabyftw.gameclient.event.events.packethandler;

import com.jabyftw.gameclient.event.EventType;
import com.jabyftw.gameclient.event.util.AbstractEvent;

/**
 * Created by Rafael on 06/02/2015.
 */
public class SendPacketErrorEvent extends AbstractEvent {

    private final Throwable throwable;

    public SendPacketErrorEvent(Throwable throwable) {
        super(EventType.SEND_PACKET_ERROR_EVENT);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
