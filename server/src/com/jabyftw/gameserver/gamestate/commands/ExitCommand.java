package com.jabyftw.gameserver.gamestate.commands;

import com.jabyftw.gameserver.ServerLauncher;
import com.jabyftw.gameserver.util.AbstractCommand;

/**
 * Created by Rafael on 06/02/2015.
 */
public class ExitCommand extends AbstractCommand {

    public ExitCommand() {
        super("exit", "close", "leave");
    }

    @Override
    public void handleCommand(String input) {
        ServerLauncher.closeApp();
    }

    @Override
    public void doOnUnregister() {
    }
}
