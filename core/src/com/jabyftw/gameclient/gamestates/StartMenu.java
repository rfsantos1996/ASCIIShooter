package com.jabyftw.gameclient.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.mapeditor.MapEditorPreparationState;
import com.jabyftw.gameclient.gamestates.play.PlayOptionsMenu;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.Constants;
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

        gameStateTitleString = Constants.GAME_NAME_CLIENT;
        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.PLAY_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.setCurrentGameState(new PlayOptionsMenu());
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
                    Main.setCurrentGameState(new ConfigMenu());
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
                    Main.setCurrentGameState(new MapEditorPreparationState());
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
            gameStateTitleColor.set(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1);
    }

    @Override
    public void drawGame(SpriteBatch batch) {
        batch.setProjectionMatrix(Main.getInstance().getHudCamera().combined);
        {
            BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_14);
            Util.drawText(font, batch, Resources.getLang(LangEnum.GAME_VERSION).replaceAll("%gameversion%", Float.toString(Constants.GAME_VERSION)), 0, 0 + (font.getLineHeight() / 2f));
            super.drawGame(batch);
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
