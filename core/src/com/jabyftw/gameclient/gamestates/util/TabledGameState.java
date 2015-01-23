package com.jabyftw.gameclient.gamestates.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

/**
 * Created by Rafael on 17/12/2014.
 */
public abstract class TabledGameState extends AbstractGameState {

    private final boolean includeEscape;

    protected String gameStateTitleString = "";
    protected Color gameStateTitleColor = Color.ORANGE.cpy();
    protected FontEnum gameStateTitleFont = FontEnum.PRESS_START_46;
    protected ButtonTable buttonTable;

    public TabledGameState(boolean includeEscape) {
        this.includeEscape = includeEscape;
    }

    @Override
    public void create() {
        desktopProcessor = ButtonTable.DesktopInputAdapter.getInputAdapter(buttonTable, includeEscape);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setProjectionMatrix(Main.getInstance().getHudCamera().combined);
        {
            if(gameStateTitleString.length() > 0) {
                // Title
                BitmapFont font = Resources.getBitmapFont(gameStateTitleFont);
                Util.drawText(
                        font,
                        batch,
                        gameStateTitleString.toUpperCase(),
                        gameStateTitleColor,
                        (Main.V_WIDTH / 2f) - ((gameStateTitleString.length() / 2f) * font.getSpaceWidth()),
                        5 * Main.V_HEIGHT / 6f
                );
            }

            buttonTable.setOffsets(Main.V_WIDTH / 2f, Main.V_HEIGHT / (gameStateTitleString.length() > 0 ? 3f : 2f));
            buttonTable.draw(batch);
        }
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
