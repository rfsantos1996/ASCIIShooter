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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jabyftw.gameclient.gamestates.StartMenu;
import com.jabyftw.gameclient.gamestates.util.GameState;
import com.jabyftw.gameclient.gamestates.util.GameStateManager;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.network.util.PacketHandler;
import com.jabyftw.gameclient.screen.MovableCamera;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.OfflinePlayerProfile;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FilesEnum;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

import java.util.LinkedList;

public class Main extends ApplicationAdapter implements Tickable {

    public static final float VERSION = 0.1f;
    public static final String WINDOW_TITLE = "<GAME NAME HERE> v" + VERSION;
    public static boolean isDebugging = false;

    public static final float STEP = 1f / 60f;
    public static final int SECONDS_TO_GARBAGE_COLLECTOR = 15;
    public static final int V_WIDTH = 740, V_HEIGHT = 480;

    public static final float PIXELS_PER_METER = 100f;

    private MovableCamera gameCamera;
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

    private SpriteBatch batch;
    private GameStateManager gameStateManager;
    private Color backgroundColor;

    private LinkedList<Float> averageDeltaTime;
    private PacketHandler packetHandler;

    private static Main main;
    private static OfflinePlayerProfile offlineProfile;
    private static OnlinePlayerProfile onlineProfile;
    private static long ticks = 0;
    private static float timeSinceLastGC = 0;

    public Main() {
        Main.main = this;
    }

    @Override
    public void create() {
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
        Resources.loadLanguage(offlineProfile.getSelectedLanguage());
        reloadMaps();

        gameCamera = new MovableCamera(0, 0, V_WIDTH, V_HEIGHT);
        gameCamera.setToOrtho(false, V_WIDTH, V_HEIGHT);

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, V_WIDTH, V_HEIGHT);
        hudViewport = new FitViewport(V_WIDTH, V_HEIGHT, hudCamera);

        batch = new SpriteBatch();
        //gameStateManager = new GameStateManager(new CreateConnectionState());
        setOnlineProfile(new OnlinePlayerProfile());
        gameStateManager = new GameStateManager(new StartMenu());
        backgroundColor = new Color(0.43f, 0.43f, 0.43f, 1);

        averageDeltaTime = new LinkedList<Float>();

        // Transform to last offlineProfile's screen
        Gdx.graphics.setDisplayMode(offlineProfile.getWidth(), offlineProfile.getHeight(), offlineProfile.isFullscreen());

        testConverter();
    }

    @Override
    public void update(float deltaTime) {
        ticks++;
        {
            // Update average Delta Time (;
            averageDeltaTime.add(deltaTime);
            if(averageDeltaTime.size() > 1f / STEP) averageDeltaTime.removeFirst();
            timeSinceLastGC += deltaTime;
        }
        gameStateManager.update(deltaTime);
        if(Gdx.input.isKeyJustPressed(Input.Keys.F))
            isDebugging = !isDebugging;
        if(Gdx.input.isKeyJustPressed(Input.Keys.G) || timeSinceLastGC > (float) Main.SECONDS_TO_GARBAGE_COLLECTOR / Main.STEP)
            gc();
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

            gameStateManager.draw(batch);

            batch.setProjectionMatrix(hudCamera.combined);
            if(Main.isDebugging) {
                BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_14);
                Util.drawText(
                        font,
                        batch,
                        "Average deltaTime: " + Main.getInstance().getAverageDeltaTime() +
                                "\nJavaHeap: " + Util.formatDecimal((double) Gdx.app.getJavaHeap() / Math.pow(10, 6), 1) + " NativeHeap: " + Util.formatDecimal((double) Gdx.app.getNativeHeap() / Math.pow(10, 6), 1),
                        Util.DEBUG_COLOR,
                        0,
                        (Main.V_HEIGHT - font.getLineHeight())
                );
            }
            batch.setProjectionMatrix(gameCamera.combined);
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
        offlineProfile.saveProfile(Resources.getFileHandle(FilesEnum.PLAYER_PROFILE_FILE));
        Resources.dispose();
        if(packetHandler != null)
            packetHandler.closeThread(false);
        super.dispose();
    }

    public void reloadMaps() {
        Resources.reloadMapsFromDirectory(Resources.getFileHandle(FilesEnum.LOCAL_MAP_DIRECTORY));
    }

    public void setCurrentGameState(GameState gameState) {
        gameStateManager.setGameState(gameState);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /*public MovableCamera getGameCamera() {
        return gameCamera;
    }*/

    public MovableCamera getGameCamera() {
        return gameCamera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public void setPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public float getAverageDeltaTime() {
        float total = 0;
        int size = averageDeltaTime.size();
        for(float deltaTime : averageDeltaTime) {
            total += deltaTime;
        }
        return total / (float) (size > 0 ? size : 1);
    }

    private void testConverter() {
        Vector2 worldTest = new Vector2(1, 3),
                screenTest = new Vector2(worldTest.x * Converter.TILE_SCALE_WIDTH, worldTest.y * Converter.TILE_SCALE_HEIGHT),
                box2dTest = new Vector2(worldTest.x * Converter.BOX2D_TILE_SCALE_WIDTH, worldTest.y * Converter.BOX2D_TILE_SCALE_HEIGHT);
        {
            System.out.println("--- Testing converter ---");
            System.out.println("World -> screen @ " + worldTest.toString() + " -> " + Converter.WORLD_COORDINATES.toScreenCoordinates(worldTest.cpy()).toString());
            System.out.println("World -> box2d @ " + worldTest.toString() + " -> " + Converter.WORLD_COORDINATES.toBox2dCoordinates(worldTest.cpy()).toString());
            System.out.println("-------------------------");
            System.out.println("Screen -> world @ " + screenTest.toString() + " -> " + Converter.SCREEN_COORDINATES.toWorldCoordinates(screenTest.cpy()).toString());
            System.out.println("Screen -> box2d @ " + screenTest.toString() + " -> " + Converter.SCREEN_COORDINATES.toBox2dCoordinates(screenTest.cpy()).toString());
            System.out.println("-------------------------");
            System.out.println("Box2d -> screen @ " + box2dTest.toString() + " -> " + Converter.BOX2D_COORDINATES.toScreenCoordinates(box2dTest.cpy()).toString());
            System.out.println("Box2d -> world @ " + box2dTest.toString() + " -> " + Converter.BOX2D_COORDINATES.toWorldCoordinates(box2dTest.cpy()).toString());
            System.out.println("--- Converter tested ---");
        }
    }

    public static Main getInstance() {
        return main;
    }

    public static long getTicksPassed() {
        return ticks;
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
