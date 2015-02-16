package com.jabyftw.gameserver.gamestate.commands;

import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameserver.Server;
import com.jabyftw.gameserver.network.ServerPacketHandler;
import com.jabyftw.gameserver.util.AbstractCommand;

/**
 * Created by Rafael on 06/02/2015.
 */
public class MemoryCommand extends AbstractCommand {

    public MemoryCommand() {
        super("memory", "mem");
    }

    @Override
    public void handleCommand(String input) {
        Runtime runtime = Runtime.getRuntime();
        Server instance = Server.getInstance();
        System.out.println();
        System.out.println("[RAM] Allocated: " + formatBytes(runtime.totalMemory() - runtime.freeMemory()) + "mb | Max: " + formatBytes(runtime.maxMemory()) + "mb");
        System.out.println("[CPU] Effectiveness: " + Util.formatDecimal(Math.min((Constants.Gameplay.STEP / instance.getAverageDeltaTime()), 1) * 100f, 1) + "% " +
                "| deltaTime: " + Util.formatDecimal(instance.getAverageDeltaTime(), 3) + "/" + Util.formatDecimal(Constants.Gameplay.STEP, 3) + "s");
        System.out.println("[NET] Bytes sent/received: " + Util.formatDecimal(ServerPacketHandler.getBytesSent() / Math.pow(10, 3), 2) + "kb" +
                "/" + Util.formatDecimal(ServerPacketHandler.getBytesReceived() / Math.pow(10, 3), 2) + "kb");
        System.out.println();
    }

    private String formatBytes(long bytes) {
        return Util.formatDecimal((float) bytes / Math.pow(10, 6), 2);
    }

    @Override
    public void doOnUnregister() {
    }
}
