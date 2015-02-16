package com.jabyftw.gameserver.network.util;

import com.jabyftw.gameserver.Server;
import com.jabyftw.gameserver.network.ServerPacketHandler;

/**
 * Created by Rafael on 13/01/2015.
 */
public class AwaitConnectionRunnable implements Runnable {

    private ServerPacketHandler packetHandler;

    public AwaitConnectionRunnable() {
    }

    public ServerPacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public void run() {
        packetHandler = new ServerPacketHandler(Server.getInstance().getServerSocket().accept(null));
        Server.getInstance().addConnectionThread(packetHandler);
    }
}
