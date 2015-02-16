package com.jabyftw.gameserver.gamestate.commands;

import com.jabyftw.gameserver.util.AbstractCommand;

/**
 * Created by Rafael on 06/02/2015.
 */
public class MyCommandHandler {

    private static final AbstractCommand commands[] = new AbstractCommand[]{
            new MemoryCommand(),
            new ExitCommand(),
            new DebugCommand(),
    };

    public static void create() {
        for(AbstractCommand command : commands) {
            command.registerCommand();
        }
    }

    public static void dispose() {
        for(AbstractCommand command : commands) {
            command.unregisterCommand();
        }
    }
}
