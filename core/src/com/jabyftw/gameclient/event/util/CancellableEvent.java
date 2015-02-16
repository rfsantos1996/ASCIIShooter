package com.jabyftw.gameclient.event.util;

/**
 * Created by Rafael on 06/02/2015.
 */
public interface CancellableEvent {

    public void setCancelled(boolean cancelled);

    public boolean isCancelled();
}
