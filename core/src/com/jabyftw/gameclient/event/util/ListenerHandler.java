package com.jabyftw.gameclient.event.util;

import com.jabyftw.gameclient.event.Listener;

/**
 * Created by Rafael on 06/02/2015.
 */
interface ListenerHandler {

    public boolean addListener(Listener listener);

    public void removeListener(Listener listener);

}
