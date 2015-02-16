package com.jabyftw.gameclient.gamestates.startup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.StartMenu;
import com.jabyftw.gameclient.gamestates.util.AbstractGameState;
import com.jabyftw.gameclient.network.ClientPacketHandler;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;
import com.sun.istack.internal.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Rafael on 12/01/2015.
 */
public class CreateConnectionState extends AbstractGameState {

    private LangEnum connectionState = LangEnum.CONNECTING;
    private ClientPacketHandler packetHandler;
    private ExecutorService threadPool;

    private float connectedTimeToChangeScreen = 0;

    @Override
    public void create() {
        threadPool = Executors.newFixedThreadPool(1, new ThreadFactory() {

            int i = 1;

            @Override
            public Thread newThread(@NotNull Runnable runnable) {
                return new Thread(runnable, "Connection thread #" + i++);
            }
        });
        desktopProcessor = new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if((Input.Keys.ENTER == keycode || Input.Keys.CENTER == keycode) && connectionState == LangEnum.CONNECTION_FAILED) {
                    connectionState = LangEnum.CONNECTING;
                    return true;
                }
                if(Input.Keys.ESCAPE == keycode) {
                    Gdx.app.exit();
                    return true;
                }
                if(Input.Keys.T == keycode && Constants.isTestBuild) {
                    Main.setOnlineProfile(new OnlinePlayerProfile());
                    Main.setPacketHandler(null);
                    Main.setCurrentGameState(new StartMenu());
                    return true;
                }
                return super.keyDown(keycode);
            }
        };
    }

    @Override
    public void update(float deltaTime) {
        if(packetHandler == null && connectionState == LangEnum.CONNECTING) {
            connectionState = LangEnum.CONNECTING_WAITING_RESPONSE;
            threadPool.submit(new TryConnectionRunnable());
        }


        if(packetHandler != null && packetHandler.getConnection().isConnected()) {
            connectedTimeToChangeScreen += deltaTime;

            if(connectedTimeToChangeScreen > Constants.Display.WAIT_TIME_AFTER_RESPONSE) {
                System.out.println("CreateConnectionState.update { \"Created connection to " + packetHandler.getConnection().getRemoteAddress() + ".\" }");

                new Thread(packetHandler, "Connection thread").start();

                Main.setPacketHandler(packetHandler);
                Main.setCurrentGameState(new LoginProfileState());
            } else {
                connectionState = LangEnum.CONNECTED;
            }
        }
    }

    @Override
    public void drawGame(SpriteBatch batch) {
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_28);

        {
            String text = Resources.getLang(connectionState);

            Util.drawText(
                    font,
                    batch,
                    text,
                    Constants.Display.V_WIDTH / 2f - ((text.length() / 2f) * font.getSpaceWidth()),
                    Constants.Display.V_HEIGHT / 2f + (font.getLineHeight() / 2f)
            );

            if(connectionState == LangEnum.CONNECTION_FAILED) {
                font = Resources.getBitmapFont(FontEnum.PRESS_START_14);
                text = Resources.getLang(LangEnum.PRESS_ENTER_TO_RECONNECT);

                Util.drawText(
                        font,
                        batch,
                        text,
                        Constants.Display.V_WIDTH / 2f - ((text.length() / 2f) * font.getSpaceWidth()),
                        Constants.Display.V_HEIGHT / 3f + (font.getLineHeight() / 2f)
                );

                text = Resources.getLang(LangEnum.PRESS_ESCAPE_TO_LEAVE);

                Util.drawText(
                        font,
                        batch,
                        text,
                        Constants.Display.V_WIDTH / 2f - ((text.length() / 2f) * font.getSpaceWidth()),
                        Constants.Display.V_HEIGHT / 3f - (font.getLineHeight())
                );
            }
        }
    }

    @Override
    public void dispose() {
        threadPool.shutdown();
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }

    private class TryConnectionRunnable implements Runnable {

        @Override
        public void run() {
            try {
                packetHandler = new ClientPacketHandler();

                if(packetHandler.getConnection() == null || !packetHandler.getConnection().isConnected()) {
                    packetHandler.closeThread(PacketKillConnection.Reason.CLIENT_LOST_CONNECTION);
                    packetHandler = null; // Connection is invalid anyway
                }
            } catch(GdxRuntimeException e) {
                //e.printStackTrace();
                System.out.println("TryConnectionRunnable.run { \"" + e.getMessage() + "\" }");
            }
            connectionState = LangEnum.CONNECTION_FAILED;
        }
    }
}
