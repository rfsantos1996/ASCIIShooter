package com.jabyftw.gameserver.gamestate.commands;

import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameserver.util.AbstractCommand;

/**
 * Created by Rafael on 07/02/2015.
 */
public class DebugCommand extends AbstractCommand {

    public DebugCommand() {
        super("debug", "debugging");
    }

    @Override
    public void handleCommand(String input) {
        Constants.isDebugging = !Constants.isDebugging;
        Constants.isDebuggingNetwork = Constants.isDebugging;
        System.out.println("Debugging = " + Constants.isDebugging);
    }

    @Override
    public void doOnUnregister() {
    }
}
