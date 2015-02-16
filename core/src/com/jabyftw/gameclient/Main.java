package com.jabyftw.gameclient;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;
import com.jabyftw.gameclient.event.EventHandler;
import com.jabyftw.gameclient.gamestates.startup.CreateConnectionState;
import com.jabyftw.gameclient.gamestates.util.GameState;
import com.jabyftw.gameclient.gamestates.util.GameStateManager;
import com.jabyftw.gameclient.network.ClientPacketHandler;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.screen.MovableCamera;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.OfflinePlayerProfile;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FilesEnum;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

import java.util.LinkedList;

public class Main extends ApplicationAdapter implements Tickable {

    private MovableCamera gameCamera;
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

    private LinkedList<Float> averageDeltaTime = new LinkedList<Float>();
    private SpriteBatch batch;
    private Color backgroundColor;

    private static Main main;
    private static Thread mainLoopThread;
    private static EventHandler eventHandler;
    private static GameStateManager gameStateManager;
    private static ClientPacketHandler packetHandler;
    private static OfflinePlayerProfile offlineProfile;
    private static OnlinePlayerProfile onlineProfile;

    private static long ticks = 0, framesRendered = 0;
    private static float timeSinceLastGC = 0;

    public Main() {
        Main.main = this;
    }

    @Override
    public void create() {
        System.out.println(Constants.GAME_NAME_CLIENT + " v" + Constants.GAME_VERSION);
        Main.mainLoopThread = Thread.currentThread();
        // Load basic resources
        Resources.loadBitmapFonts();
        Resources.loadTextures();
        Resources.loadAnimations();
        Resources.loadCommonFiles();
        {
            // Load player offlineProfile & language
            FileHandle playerProfileFile = Resources.getFileHandle(FilesEnum.PLAYER_PROFILE_FILE);

            offlineProfile = playerProfileFile.exists() ?
                    OfflinePlayerProfile.readProfile(playerProfileFile) :
                    new OfflinePlayerProfile();
        }
        Resources.loadLanguage(offlineProfile.getSelectedLanguage(), true);
        reloadMaps();
        WeaponProperties.initializeWeapons();

        gameCamera = new MovableCamera(0, 0, Constants.Display.V_WIDTH, Constants.Display.V_HEIGHT);
        gameCamera.setToOrtho(false, Constants.Display.V_WIDTH, Constants.Display.V_HEIGHT);

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Constants.Display.V_WIDTH, Constants.Display.V_HEIGHT);
        hudViewport = new FitViewport(Constants.Display.V_WIDTH, Constants.Display.V_HEIGHT, hudCamera);

        batch = new SpriteBatch();
        backgroundColor = new Color(0.43f, 0.43f, 0.43f, 1);

        eventHandler = new EventHandler();
        gameStateManager = new GameStateManager(new CreateConnectionState());

        // Transform to last offlineProfile's screen
        Gdx.graphics.setDisplayMode(offlineProfile.getWidth(), offlineProfile.getHeight(), offlineProfile.isFullscreen());
    }

    @Override
    public void update(float deltaTime) {
        ticks++;
        {
            if(packetHandler != null)
                packetHandler.update(deltaTime);

            eventHandler.update(deltaTime);
            gameStateManager.update(deltaTime);

            { // Update average Delta Time (;
                averageDeltaTime.add(deltaTime);
                if(averageDeltaTime.size() > 1f / Constants.Gameplay.STEP) averageDeltaTime.removeFirst();
                timeSinceLastGC += deltaTime;
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.F) && Constants.isTestBuild) {
                Constants.isDebugging = !Constants.isDebugging;
                Constants.isDebuggingNetwork = Constants.isDebugging;
            }

            if((Gdx.input.isKeyJustPressed(Input.Keys.G) && Constants.isTestBuild) || timeSinceLastGC > Constants.Gameplay.SECONDS_TO_GARBAGE_COLLECTOR)
                gc();
        }
    }

    @Override
    public void render() {
        {
            // Update stuff
            update(Gdx.graphics.getDeltaTime());
        }
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        {
            // Draw stuff
            hudViewport.apply();
            gameCamera.update();
            {
                batch.setProjectionMatrix(gameCamera.combined);
                gameStateManager.drawGame(batch);
            }
            {
                batch.setProjectionMatrix(hudCamera.combined);
                gameStateManager.drawHUD(batch);
            }
            if(Constants.isDebugging) {
                BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_14);
                Util.drawText(
                        font,
                        batch,
                        "Average deltaTime: " + Main.getInstance().getAverageDeltaTime() + " framesRendered: " + framesRendered +

                                "\nBytes sent: " + Util.formatDecimal((packetHandler != null ? ClientPacketHandler.getBytesSent() : 0) / Math.pow(10, 3), 2) + "kb " +
                                "Bytes received: " + Util.formatDecimal((packetHandler != null ? ClientPacketHandler.getBytesReceived() : 0) / Math.pow(10, 3), 2) + "kb" +

                                "\nJavaHeap: " + Util.formatDecimal((double) Gdx.app.getJavaHeap() / Math.pow(10, 6), 1) +
                                " NativeHeap: " + Util.formatDecimal((double) Gdx.app.getNativeHeap() / Math.pow(10, 6), 1),
                        Constants.Util.DEBUG_COLOR,
                        0,
                        (Constants.Display.V_HEIGHT - font.getLineHeight())
                );
            }
            batch.flush();
            framesRendered++;
        }
    }

    @Override
    public void resize(int width, int height) {
        hudViewport.update(width, height, false);
        offlineProfile.setDisplayMode(width, height, Gdx.graphics.isFullscreen());
    }

    @Override
    public void dispose() {
        gameStateManager.dispose();
        if(!offlineProfile.isNewProfile())
            offlineProfile.saveProfile();
        batch.dispose();
        Resources.dispose();
        if(packetHandler != null)
            packetHandler.closeThread(PacketKillConnection.Reason.CLIENT_CLOSING_CONNECTION);
        super.dispose();
    }

    public void reloadMaps() {
        Resources.reloadMapsFromDirectory(Resources.getFileHandle(FilesEnum.LOCAL_MAP_DIRECTORY));
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public MovableCamera getGameCamera() {
        return gameCamera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }

    float getAverageDeltaTime() {
        float total = 0;
        int size = averageDeltaTime.size();

        for(float deltaTime : averageDeltaTime) {
            total += deltaTime;
        }

        return total / (float) (size > 0 ? size : 1);
    }

    public static Main getInstance() {
        return main;
    }

    public static long getTicksPassed() {
        return ticks;
    }

    public static Thread getMainLoopThread() {
        return mainLoopThread;
    }

    public static void setCurrentGameState(final GameState gameState) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                gameStateManager.setGameState(gameState);
            }
        });
    }

    public static void setPacketHandler(ClientPacketHandler packetHandler) {
        Main.packetHandler = packetHandler;
    }

    public static ClientPacketHandler getPacketHandler() {
        return packetHandler;
    }

    public static EventHandler getEventHandler() {
        return eventHandler;
    }

    public static OfflinePlayerProfile getOfflineProfile() {
        return offlineProfile;
    }

    public static OnlinePlayerProfile getOnlineProfile() {
        return onlineProfile;
    }

    public static void setOnlineProfile(OnlinePlayerProfile onlineProfile) {
        Main.onlineProfile = onlineProfile;
    }

    public static void gc() {
        System.gc();
        timeSinceLastGC = 0;
    }
}
