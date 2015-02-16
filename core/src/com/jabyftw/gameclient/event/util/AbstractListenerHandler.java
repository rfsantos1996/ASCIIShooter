package com.jabyftw.gameclient.event.util;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.event.Listener;

/**
 * Created by Rafael on 06/02/2015.
 */
public abstract class AbstractListenerHandler implements ListenerHandler, Disposable {

    protected final Array<Listener> listenerArray = new Array<Listener>();
    //protected Listener currentPacketListener;

    /*public void setCurrentPacketListener(Listener currentPacketListener) {
        if(this.currentPacketListener != null)
            removeListener(this.currentPacketListener);
        this.currentPacketListener = currentPacketListener;
        if(this.currentPacketListener != null)
            addListener(this.currentPacketListener);
    }*/

    @Override
    public boolean addListener(Listener listener) {
        if(listener == null)
            throw new NullPointerException("Listener can't be null!");
        if(!listenerArray.contains(listener, true))
            listenerArray.add(listener);
        return true;
    }

    @Override
    public void removeListener(Listener listener) {
        listenerArray.removeValue(listener, true);
    }

    @Override
    public void dispose() {
        listenerArray.clear();
    }
}
