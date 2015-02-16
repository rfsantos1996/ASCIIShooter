package com.jabyftw.gameclient.event.util;

import com.jabyftw.gameclient.event.EventType;

/**
 * Created by Rafael on 06/02/2015.
 */
public abstract class AbstractCancellableEvent extends AbstractEvent implements CancellableEvent {

    private boolean cancelled = false;

    protected AbstractCancellableEvent(EventType eventType) {
        super(eventType);
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
