package com.jabyftw.gameclient.event.util;

import com.jabyftw.gameclient.event.EventType;

/**
 * Created by Rafael on 06/02/2015.
 */
public interface Event {

    public long getId();

    public EventType getEventType();

}
