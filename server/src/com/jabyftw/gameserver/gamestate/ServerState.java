package com.jabyftw.gameserver.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.network.util.PacketHandler;
import com.jabyftw.gameclient.util.Drawable;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameserver.network.ServerPacketHandler;
import com.jabyftw.gameserver.util.AwaitConnectionRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Rafael on 12/01/2015.
 */
public class ServerState implements Drawable, Tickable, Disposable {

    public static final int NUMBER_OF_THREADS = 24;

    private static final Array<ServerPacketHandler> connectionThreads = new Array<ServerPacketHandler>();

    private ExecutorService threadPool;
    protected ServerSocket serverSocket;

    private Future<?> lastSubmission;

    public void create() {
        threadPool = Executors.newFixedThreadPool(NUMBER_OF_THREADS, new ThreadFactory() {

            int i = 1;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Connection thread " + i++);
            }
        });
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, PacketHandler.CONNECTION_PORT, null);
    }

    @Override
    public void update(float deltaTime) {
        if(lastSubmission == null || lastSubmission.isDone() || lastSubmission.isCancelled())
            lastSubmission = threadPool.submit(new AwaitConnectionRunnable(serverSocket));
    }

    @Override
    public void draw(SpriteBatch batch) {
    }

    @Override
    public void dispose() {
        for(ServerPacketHandler packetHandler : connectionThreads) {
            packetHandler.closeThread(false);
        }
        threadPool.shutdown();
        serverSocket.dispose();
    }

    public static void addConnectionThread(ServerPacketHandler packetHandler) {
        if(!connectionThreads.contains(packetHandler, true))
            connectionThreads.add(packetHandler);
        else
            packetHandler.closeThread(false);
    }
}
