package com.jabyftw.gameclient.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.mapeditor.MapEditorPreparationState;
import com.jabyftw.gameclient.gamestates.play.PrePlayState;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 05/12/2014.
 */
public class StartMenu extends TabledGameState {

    public StartMenu() {
        super(false);
    }

    @Override
    public void create() {
        FontEnum font = FontEnum.PRESS_START_28;

        gameStateTitle = Resources.getLang(LangEnum.GAME_TITLE).toUpperCase();
        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.PLAY_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(new PrePlayState());
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.SETTINGS_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(new ConfigMenu());
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.MAP_EDITOR_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }


                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(new MapEditorPreparationState());
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.EXIT_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Gdx.app.exit();
                }
            });
        }
        super.create();
    }

    @Override
    public void update(float deltaTime) {
        if(Main.getTicksPassed() % 6 == 0)
            gameStateTitleColor.set((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setProjectionMatrix(Main.getInstance().getHudCamera().combined);
        {
            BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_14);
            Util.drawText(font, batch, "version " + Main.VERSION, 0, 0 + (font.getLineHeight() / 2f));
            super.draw(batch);
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
