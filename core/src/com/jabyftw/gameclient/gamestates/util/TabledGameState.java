package com.jabyftw.gameclient.gamestates.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

/**
 * Created by Rafael on 17/12/2014.
 */
public abstract class TabledGameState extends AbstractGameState {

    private final boolean includeEscape;

    protected final Color gameStateTitleColor = Color.ORANGE.cpy();
    protected FontEnum gameStateTitleFont = FontEnum.PRESS_START_46;
    protected int gameStateTitleShadowOffset = 2;
    protected String gameStateTitleString = "";
    protected ButtonTable buttonTable;

    protected TabledGameState(boolean includeEscape) {
        this.includeEscape = includeEscape;
    }

    @Override
    public void create() {
        desktopProcessor = ButtonTable.DesktopInputAdapter.getInputAdapter(buttonTable, includeEscape);
    }

    @Override
    public void drawGame(SpriteBatch batch) {
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        if(gameStateTitleString.length() > 0) {
            // Title
            BitmapFont font = Resources.getBitmapFont(gameStateTitleFont);
            Util.drawText(
                    font,
                    batch,
                    gameStateTitleString.toUpperCase(),
                    gameStateTitleColor,
                    (Constants.Display.V_WIDTH / 2f) - ((gameStateTitleString.length() / 2f) * font.getSpaceWidth()),
                    5 * Constants.Display.V_HEIGHT / 6f,
                    gameStateTitleShadowOffset,
                    gameStateTitleShadowOffset
            );
        }

        buttonTable.setOffsets(Constants.Display.V_WIDTH / 2f, Constants.Display.V_HEIGHT / (gameStateTitleString.length() > 0 ? 3f : 2f));
        buttonTable.drawHUD(batch);
    }

    @Override
    public void update(float deltaTime) {
        buttonTable.update(deltaTime);
    }

    @Override
    public void dispose() {
        buttonTable.dispose();
    }
}
