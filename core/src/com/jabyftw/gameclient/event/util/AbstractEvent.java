package com.jabyftw.gameclient.event.util;

import com.jabyftw.gameclient.event.EventType;

/**
 * Created by Rafael on 06/02/2015.
 */
public abstract class AbstractEvent implements Event {

    private static long lastId = 0;

    private final EventType eventType;
    private final long id;

    protected AbstractEvent(EventType eventType) {
        this.eventType = eventType;
        this.id = lastId++;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }
}
