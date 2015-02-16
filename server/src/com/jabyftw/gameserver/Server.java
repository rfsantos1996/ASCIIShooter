package com.jabyftw.gameserver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglNet;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;
import com.jabyftw.gameclient.event.EventHandler;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FilesEnum;
import com.jabyftw.gameserver.gamestate.commands.MyCommandHandler;
import com.jabyftw.gameserver.network.ServerPacketHandler;
import com.jabyftw.gameserver.network.util.AwaitConnectionRunnable;
import com.jabyftw.gameserver.network.util.MySQLConnection;
import com.jabyftw.gameserver.util.Command;
import com.jabyftw.gameserver.util.Properties;
import com.sun.istack.internal.NotNull;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Rafael on 12/01/2015.
 */
public class Server implements Tickable, Disposable {

    public Server() {
        Server.server = this;
    }

    private static Server server;

    private static final Array<Command> commandArray = new Array<Command>();
    private static EventHandler eventHandler = new EventHandler();
    private static float timeSinceLastGC = 0;

    private final Array<ServerPacketHandler> connectionThreads = new Array<ServerPacketHandler>();
    private final LinkedList<Float> averageDeltaTime = new LinkedList<Float>();

    private ExecutorService connectionThreadPool;
    private ServerSocket serverSocket;
    private Properties properties;
    private MySQLConnection mySQLConnection;

    public void create() {
        System.out.println(Constants.GAME_NAME_SERVER + " v" + Constants.GAME_VERSION);
        {
            Gdx.files = new LwjglFiles();
            Gdx.net = new LwjglNet();
        }
        // Load basic resources
        Resources.loadCommonFiles();
        Resources.reloadMapsFromDirectory(Resources.getFileHandle(FilesEnum.LOCAL_MAP_DIRECTORY));
        WeaponProperties.initializeWeapons();
        MyCommandHandler.create();

        { // Properties
            FileHandle fileHandle = Resources.getFileHandle(FilesEnum.SERVER_PROPERTIES_DIRECTORY);
            if(!fileHandle.exists()) {
                this.properties = new Properties();
                System.out.println("Created new server.properties");
            } else {
                this.properties = new Json().fromJson(Properties.class, fileHandle);
                System.out.println("Loaded server.properties");
            }
        }
        { // Set up MySQL
            try {
                this.mySQLConnection = new MySQLConnection(
                        properties.getMysqlRevision(),
                        properties.getMysqlUsername(),
                        properties.getMysqlPassword(),
                        properties.getMysqlHost(),
                        properties.getMysqlPort(),
                        properties.getMysqlDatabase()
                );
            } catch(SQLException e) {
                //e.printStackTrace();
                System.out.println("Couldn't start MySQL: " + e.getMessage());
                //return; // TODO: after adding the driver, make it return
            }
        }
        { // Connection thread pool
            this.connectionThreadPool = Executors.newFixedThreadPool(Constants.Multiplayer.NUMBER_OF_SERVER_THREADS, new ThreadFactory() {

                private int lastIndex = 1;

                @Override
                public Thread newThread(@NotNull Runnable runnable) {
                    return new Thread(runnable, "Connection thread " + lastIndex++);
                }
            });
        }
        { // Start up connections
            this.serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, Constants.Multiplayer.CONNECTION_PORT, null);
            waitNewConnection();
            System.out.println("Listening at port " + Constants.Multiplayer.CONNECTION_PORT);
        }
    }

    @Override
    public void update(float deltaTime) {
        eventHandler.update(deltaTime);
        { // Update connections
            for(ServerPacketHandler connectionThread : connectionThreads) {
                connectionThread.update(deltaTime);
            }
        }

        { // Update average Delta Time (;
            averageDeltaTime.add(deltaTime);
            if(averageDeltaTime.size() > (1f / Constants.Gameplay.STEP)) averageDeltaTime.removeFirst();
            timeSinceLastGC += deltaTime;
        }

        if(timeSinceLastGC > Constants.Gameplay.SECONDS_TO_GARBAGE_COLLECTOR)
            gc();
    }

    public void handleInput(String input) {
        boolean handled = false;

        for(Command command : commandArray) {
            if(command.canHandleCommand(input))
                try {
                    command.handleCommand(input);
                    handled = true;
                } catch(Throwable throwable) { // Do not kill server
                    throwable.printStackTrace();
                }
        }

        if(!handled) System.out.println("? Unknown command: \"" + input + "\"");
    }

    @Override
    public void dispose() {
        MyCommandHandler.dispose();
        { // Close commands
            for(Command command : commandArray) {
                command.doOnUnregister();
            }
            commandArray.clear();
        }
        { // Close connection
            for(ServerPacketHandler packetHandler : connectionThreads) {
                packetHandler.closeThread(PacketKillConnection.Reason.SERVER_SHUTTING_DOWN);
            }
            connectionThreadPool.shutdown();
            serverSocket.dispose();
            if(mySQLConnection != null)
                mySQLConnection.dispose();
            {
                FileHandle fileHandle = Resources.getFileHandle(FilesEnum.SERVER_PROPERTIES_DIRECTORY);
                String jsonPrint = new Json().prettyPrint(properties); // Make it more user-readable
                fileHandle.writeString(jsonPrint, false, "UTF-8");
            }
            System.out.println("Closed server threads, all connections and socket + saved server.properties");
        }
        Resources.dispose();
    }

    public boolean connectionThreadExists(String connectionName) {
        for(ServerPacketHandler connectionThread : new Array.ArrayIterator<ServerPacketHandler>(connectionThreads)) {
            if(connectionThread.getConnectionName().equalsIgnoreCase(connectionName))
                return true;
        }
        return false;
    }

    public void addConnectionThread(ServerPacketHandler packetHandler) {
        if(!connectionThreads.contains(packetHandler, false)) {
            connectionThreads.add(packetHandler);
            connectionThreadPool.submit(packetHandler);
            waitNewConnection();
        } else {
            System.out.println("Packet handler already exists.. Eliminating.");
            //connectionThreads.removeValue(packetHandler, false);
            packetHandler.closeThread(PacketKillConnection.Reason.SERVER_UNKNOWN);
        }
    }

    public void removeConnectionThread(ServerPacketHandler packetHandler) {
        connectionThreads.removeValue(packetHandler, false);
    }

    private void waitNewConnection() {
        connectionThreadPool.submit(new AwaitConnectionRunnable());
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Properties getProperties() {
        return properties;
    }

    public float getAverageDeltaTime() {
        float total = 0;
        int size = averageDeltaTime.size();

        for(float deltaTime : averageDeltaTime) {
            total += deltaTime;
        }

        return total / (float) (size > 0 ? size : 1);
    }

    public static Server getInstance() {
        return server;
    }

    public static EventHandler getEventHandler() {
        return eventHandler;
    }

    public static void gc() {
        System.gc();
        timeSinceLastGC = 0;
    }

    public static boolean registerCommand(Command command) {
        for(Command commands : commandArray) {
            if(commands.equals(command))
                throw new IllegalStateException("Can't register command when another name is already registered.");
        }
        Server.commandArray.add(command);
        return true;
    }

    public static void unregisterCommand(Command command) {
        command.doOnUnregister();
        Server.commandArray.removeValue(command, false);
    }
}
