package com.jabyftw.gameclient.gamestates.startup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.event.events.network.PlayerLoginEvent;
import com.jabyftw.gameclient.event.events.network.PlayerRegisterEvent;
import com.jabyftw.gameclient.gamestates.StartMenu;
import com.jabyftw.gameclient.gamestates.util.AbstractGameState;
import com.jabyftw.gameclient.gamestates.util.PseudoGameState;
import com.jabyftw.gameclient.network.listeners.LoggedInListener;
import com.jabyftw.gameclient.network.packets.client.PacketLoginRequest;
import com.jabyftw.gameclient.network.packets.client.PacketRegisterRequest;
import com.jabyftw.gameclient.network.packets.server.PacketLoginResponse;
import com.jabyftw.gameclient.network.packets.server.PacketRegisterResponse;
import com.jabyftw.gameclient.network.util.LoginResponse;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.OfflinePlayerProfile;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 05/02/2015.
 */
public class WaitingLoginState extends AbstractGameState implements PseudoGameState, Listener {

    private final String username, password, secondPassword;

    private LoginResponse loginResponse = null;
    private int numberOfTries = 0;
    private float timeSinceAsked = 0;
    private float timeSinceLoggedIn = -1;

    public WaitingLoginState(String username, String password) {
        this(username, password, null);
    }

    public WaitingLoginState(String username, String password, String secondPassword) {
        super();
        this.username = username;
        this.password = password;
        this.secondPassword = secondPassword;
        Main.getPacketHandler().addListener(this);
        Main.getEventHandler().addListener(this);
    }

    @PacketListener(deliverType = PacketListener.DeliverType.RECEIVED)
    public void handlePacket(final PacketLoginResponse packetLoginResponse) {
        this.loginResponse = packetLoginResponse.getLoginResponse();
        if(loginResponse == LoginResponse.SUCCESSFUL_LOGIN) {
            PlayerLoginEvent loginEvent;

            if(packetLoginResponse instanceof PacketRegisterResponse)
                loginEvent = new PlayerRegisterEvent(((PacketRegisterResponse) packetLoginResponse));
            else
                loginEvent = new PlayerLoginEvent(packetLoginResponse);

            Main.getEventHandler().callEvent(loginEvent);
        }
    }

    @EventListener
    public void onLogin(PlayerLoginEvent loginEvent) {
        OfflinePlayerProfile offlinePlayerProfile = Main.getOfflineProfile();
        offlinePlayerProfile.setPlayerName(username);
        offlinePlayerProfile.saveProfile();

        Main.setOnlineProfile(loginEvent.getLoginResponse().getPlayerProfile());
        this.timeSinceLoggedIn = 0;
    }

    @Override
    public void create() {
        desktopProcessor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if((Input.Keys.ENTER == keycode || Input.Keys.CENTER == keycode) && showOtherButtons()) {
                    Main.setCurrentGameState(null);
                    return true;
                }
                if(Input.Keys.ESCAPE == keycode && loginResponse != LoginResponse.SUCCESSFUL_LOGIN) {
                    Gdx.app.exit();
                    return true;
                }
                return super.keyDown(keycode);
            }
        };
        askForLogin();
    }

    @Override
    public void update(float deltaTime) {
        timeSinceAsked += deltaTime;
        if(timeSinceAsked > Constants.Multiplayer.CLIENT_TIMEOUT_TIME && loginResponse == null) { // Try again if didn't logged in yet
            if(numberOfTries < 3) {
                askForLogin();
            } else {
                loginResponse = LoginResponse.UNKNOWN_ERROR;
            }
        }

        if(timeSinceLoggedIn >= 0) {
            timeSinceLoggedIn += deltaTime;
            if(timeSinceLoggedIn > Constants.Display.WAIT_TIME_AFTER_RESPONSE)
                Main.setCurrentGameState(new StartMenu());
        }
    }

    @Override
    public void drawGame(SpriteBatch batch) {
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_28);
        {
            String text = Resources.getLang(loginResponse != null ? loginResponse.getLangEnum() : LangEnum.CONNECTING_WAITING_RESPONSE);

            Util.drawText(
                    font,
                    batch,
                    text,
                    Constants.Display.V_WIDTH / 2f - ((text.length() / 2f) * font.getSpaceWidth()),
                    Constants.Display.V_HEIGHT / 2f + (font.getLineHeight() / 2f)
            );

            if(showOtherButtons()) {
                font = Resources.getBitmapFont(FontEnum.PRESS_START_14);
                text = Resources.getLang(LangEnum.PRESS_ENTER_TO_GO_BACK);

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
    public boolean shouldRegisterInput() {
        return true;
    }

    @Override
    public void dispose() {
        Main.getEventHandler().removeListener(this);
        if(Main.getPacketHandler() != null) {
            Main.getPacketHandler().removeListener(this);
            Main.getPacketHandler().addListener(new LoggedInListener());
        }
    }

    private void askForLogin() {
        PacketLoginRequest loginRequest = secondPassword != null ? new PacketRegisterRequest(username, password, secondPassword) : new PacketLoginRequest(username, password);
        Main.getPacketHandler().addPacketToQueue(loginRequest);
        this.timeSinceAsked = 0;
        this.numberOfTries++;
    }

    private boolean showOtherButtons() {
        return loginResponse != null && loginResponse != LoginResponse.SUCCESSFUL_LOGIN;
    }
}
