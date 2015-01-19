package com.jabyftw.gameserver.util;

import com.badlogic.gdx.net.ServerSocket;
import com.jabyftw.gameserver.gamestate.ServerState;
import com.jabyftw.gameserver.network.ServerPacketHandler;

/**
 * Created by Rafael on 13/01/2015.
 */
public class AwaitConnectionRunnable implements Runnable {

    private final ServerSocket serverSocket;

    public AwaitConnectionRunnable(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        ServerPacketHandler packetHandler = new ServerPacketHandler(serverSocket.accept(null));
        ServerState.addConnectionThread(packetHandler);
        packetHandler.run();
    }
}
