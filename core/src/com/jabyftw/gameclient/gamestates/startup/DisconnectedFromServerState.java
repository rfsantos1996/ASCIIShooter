package com.jabyftw.gameclient.gamestates.startup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.util.AbstractGameState;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 07/02/2015.
 */
public class DisconnectedFromServerState extends AbstractGameState {

    private final String disconnectMessage;

    public DisconnectedFromServerState(String disconnectMessage) {
        this.disconnectMessage = disconnectMessage;
    }

    @Override
    public void create() {
        desktopProcessor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if((Input.Keys.ENTER == keycode || Input.Keys.CENTER == keycode)) {
                    Main.setCurrentGameState(new CreateConnectionState());
                    return true;
                }
                if(Input.Keys.ESCAPE == keycode) {
                    Gdx.app.exit();
                    return true;
                }
                return super.keyDown(keycode);
            }
        };
        Main.getPacketHandler().closeThread(null);
        Main.setPacketHandler(null);
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void drawGame(SpriteBatch batch) {
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_28);
        {
            String text = disconnectMessage;
            {
                Util.drawText(
                        font,
                        batch,
                        text,
                        Constants.Display.V_WIDTH / 2f - ((text.length() / 2f) * font.getSpaceWidth()),
                        Constants.Display.V_HEIGHT / 2f + (font.getLineHeight() / 2f)
                );
            }

            font = Resources.getBitmapFont(FontEnum.PRESS_START_14);

            {
                text = Resources.getLang(LangEnum.PRESS_ENTER_TO_RECONNECT);
                Util.drawText(
                        font,
                        batch,
                        text,
                        Constants.Display.V_WIDTH / 2f - ((text.length() / 2f) * font.getSpaceWidth()),
                        Constants.Display.V_HEIGHT / 3f + (font.getLineHeight() / 2f)
                );
            }

            {
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
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }
}
