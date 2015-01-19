package com.jabyftw.gameserver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.util.Tickable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FilesEnum;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameserver.gamestate.ServerState;

import java.util.LinkedList;

/**
 * Created by Rafael on 12/01/2015.
 */
public class Server extends ApplicationAdapter implements Tickable {

    private static boolean isDebugging = true;

    private ServerState serverState;

    private OrthographicCamera hudCamera;
    private Viewport hudViewport;

    private Color backgroundColor;
    private SpriteBatch batch;

    private LinkedList<Float> averageDeltaTime;
    private static float timeSinceLastGC = 0;

    @Override
    public void create() {
        // Load basic resources
        Resources.loadBitmapFonts();
        Resources.loadCommonFiles();
        Resources.reloadMapsFromDirectory(Resources.getFileHandle(FilesEnum.LOCAL_MAP_DIRECTORY));

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Main.V_WIDTH, Main.V_HEIGHT);
        hudViewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, hudCamera);

        batch = new SpriteBatch();
        backgroundColor = new Color(0.43f, 0.43f, 0.43f, 1);

        serverState = new ServerState();
        serverState.create();

        averageDeltaTime = new LinkedList<Float>();
    }

    @Override
    public void update(float deltaTime) {
        {
            // Update average Delta Time (;
            averageDeltaTime.add(deltaTime);
            if(averageDeltaTime.size() > 1f / Main.STEP) averageDeltaTime.removeFirst();
            timeSinceLastGC += deltaTime;
        }
        serverState.update(deltaTime);
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
            batch.setProjectionMatrix(hudCamera.combined);
            hudCamera.update();
            serverState.draw(batch);
            if(isDebugging) {
                BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_14);
                Util.drawText(
                        font,
                        batch,
                        "Average deltaTime: " + getAverageDeltaTime() +
                                "\nJavaHeap: " + Util.formatDecimal((double) Gdx.app.getJavaHeap() / Math.pow(10, 6), 1) + " NativeHeap: " + Util.formatDecimal((double) Gdx.app.getNativeHeap() / Math.pow(10, 6), 1),
                        Util.DEBUG_COLOR,
                        0,
                        (Main.V_HEIGHT - font.getLineHeight())
                );
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        hudViewport.update(width, height);
    }

    @Override
    public void dispose() {
        Resources.dispose();
        super.dispose();
    }

    public float getAverageDeltaTime() {
        float total = 0;
        int size = averageDeltaTime.size();
        for(float deltaTime : averageDeltaTime) {
            total += deltaTime;
        }
        return total / (float) (size > 0 ? size : 1);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public static void gc() {
        System.gc();
        timeSinceLastGC = 0;
    }
}
