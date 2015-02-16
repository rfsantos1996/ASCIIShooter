package com.jabyftw.gameserver.util;

import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameserver.Server;

/**
 * Created by Rafael on 06/02/2015.
 */
public abstract class AbstractCommand implements Command {

    private final Array<String> commandNames = new Array<String>();
    private final String primaryCommand;

    public AbstractCommand(String commandName, String... commandNames) {
        this.commandNames.add((primaryCommand = commandName.toLowerCase()));
        for(String command : commandNames) {
            this.commandNames.add(command.toLowerCase());
        }
    }

    @Override
    public Array<String> getCommandNames() {
        return commandNames;
    }

    @Override
    public String getPrimaryCommand() {
        return primaryCommand;
    }

    @Override
    public boolean canHandleCommand(String input) {
        return commandNames.contains(input.toLowerCase(), false);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof AbstractCommand) {
            for(String input : commandNames) {
                if(((AbstractCommand) obj).canHandleCommand(input))
                    return true;
            }
        }
        return false;
    }

    public void registerCommand() {
        Server.registerCommand(this);
    }

    public void unregisterCommand() {
        Server.unregisterCommand(this);
    }
}
