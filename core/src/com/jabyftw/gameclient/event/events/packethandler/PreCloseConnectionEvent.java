package com.jabyftw.gameclient.event.events.packethandler;

import com.jabyftw.gameclient.event.EventType;
import com.jabyftw.gameclient.event.util.AbstractEvent;

/**
 * Created by Rafael on 06/02/2015.
 */
public class PreCloseConnectionEvent extends AbstractEvent {

    public PreCloseConnectionEvent() {
        super(EventType.PRE_CLOSE_CONNECTION_EVENT);
    }
}
