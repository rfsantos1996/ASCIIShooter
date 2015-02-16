package com.jabyftw.gameclient.event.events.packethandler;

import com.jabyftw.gameclient.event.EventType;
import com.jabyftw.gameclient.event.util.AbstractEvent;

/**
 * Created by Rafael on 06/02/2015.
 */
public class PostCloseConnectionEvent extends AbstractEvent {

    public PostCloseConnectionEvent() {
        super(EventType.POST_CLOSE_CONNECTION_EVENT);
    }
}
