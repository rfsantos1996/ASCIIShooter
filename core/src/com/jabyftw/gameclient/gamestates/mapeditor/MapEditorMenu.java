package com.jabyftw.gameclient.gamestates.mapeditor;

import com.badlogic.gdx.graphics.Color;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.ConfigMenu;
import com.jabyftw.gameclient.gamestates.StartMenu;
import com.jabyftw.gameclient.gamestates.play.playstate.PlayState;
import com.jabyftw.gameclient.gamestates.util.PseudoGameState;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Isa on 02/01/2015.
 */
public class MapEditorMenu extends TabledGameState implements PseudoGameState {

    private final MapEditorState previousState;

    public MapEditorMenu(MapEditorState mapEditorState) {
        super(true);
        this.previousState = mapEditorState;
    }

    @Override
    public void create() {
        FontEnum font = FontEnum.PRESS_START_28;

        gameStateTitleFont = font;
        gameStateTitleString = Resources.getLang(LangEnum.MAP_EDITOR_OPTIONS);
        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.TEST_MAP), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    previousState.getMap().setShouldDispose(false);
                    Main.setCurrentGameState(new PlayState(previousState.getMap(), true));
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
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.BACK_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.setCurrentGameState(null);
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.BACK_TO_TITLE_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.setCurrentGameState(new StartMenu());
                }
            });
        }
        super.create();
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }
}
