package com.jabyftw.gameserver.util;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Rafael on 06/02/2015.
 */
public interface Command {

    public Array<String> getCommandNames();

    public String getPrimaryCommand();

    public boolean canHandleCommand(String input);

    public void handleCommand(String input);

    public void doOnUnregister();

}
