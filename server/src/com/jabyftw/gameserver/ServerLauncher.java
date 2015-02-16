package com.jabyftw.gameserver;

import com.jabyftw.gameclient.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rafael on 12/01/2015.
 */
public class ServerLauncher {

    public static void main(String[] args) {
        Thread mainLoopThread = new Thread(new MainLoopRunnable(), "Main loop");
        mainLoopThread.start();
    }

    public static void closeApp() {
        MainLoopRunnable.closeApp();
    }

    private static class MainLoopRunnable implements Runnable {

        private static boolean running = true;
        private final Server server;
        private long lastTick = System.nanoTime();

        public MainLoopRunnable() {
            this.server = new Server();
        }

        @Override
        public void run() {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            server.create();
            while(running) {
                // Make and reset deltaTime (in seconds) for update stuff
                float deltaTime = (System.nanoTime() - lastTick) / (float) TimeUnit.SECONDS.toNanos(1);
                lastTick = System.nanoTime();

                long start = System.nanoTime();
                {
                    // Update everything
                    try {
                        while(bufferedReader.ready()) // Server commands!
                            server.handleInput(bufferedReader.readLine());

                        server.update(deltaTime);
                    } catch(Throwable throwable) { // Do not let the server die );
                        throwable.printStackTrace();
                    }
                }
                long updateTime = System.nanoTime() - start;
                {
                    // Wait for next tick
                    long maximumWaitTime = (long) (Constants.Gameplay.STEP * (float) TimeUnit.SECONDS.toNanos(1));
                    long waitTime = maximumWaitTime - updateTime;

                    if(waitTime > 0)
                        synchronized(this) {
                            try {
                                int waitTimeNanos = (int) (waitTime % TimeUnit.MILLISECONDS.toNanos(1));
                                long waitTimeMillis = (long) ((waitTime - waitTimeNanos) / (float) TimeUnit.MILLISECONDS.toNanos(1));
                                // Wait with nanosecond precision
                                wait(waitTimeMillis, waitTimeNanos);
                            } catch(InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                }
            }
            server.dispose();
        }

        private static void closeApp() {
            MainLoopRunnable.running = false;
        }
    }
}
