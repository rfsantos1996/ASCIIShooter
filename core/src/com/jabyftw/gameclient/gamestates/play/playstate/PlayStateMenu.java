package com.jabyftw.gameclient.gamestates.play.playstate;

import com.badlogic.gdx.graphics.Color;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.ConfigMenu;
import com.jabyftw.gameclient.gamestates.StartMenu;
import com.jabyftw.gameclient.gamestates.mapeditor.MapEditorState;
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
public class PlayStateMenu extends TabledGameState implements PseudoGameState {

    private final PlayState playState;
    private final boolean editableMap;

    public PlayStateMenu(PlayState playState, boolean editableMap) {
        super(true);
        this.playState = playState;
        this.editableMap = editableMap;
    }

    @Override
    public void create() {
        gameStateTitleFont = FontEnum.PRESS_START_28;
        gameStateTitleString = Resources.getLang(LangEnum.PAUSE_MENU_TITLE);

        buttonTable = new ButtonTable(gameStateTitleFont, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.CHANGE_LAYOUT), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(new ChangeLayoutMenu(playState));
                }
            });
        }
        if(editableMap) {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.EDIT_MAP), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    playState.getMap().setShouldDispose(false);
                    Main.getInstance().setCurrentGameState(new MapEditorState(playState.getMap()));
                }
            });
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.EXPORT_MAP), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    playState.getMap().saveMap();
                    Main.getInstance().reloadMaps();
                    Main.getInstance().setCurrentGameState(new StartMenu());
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
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.BACK_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(null);
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
                    Main.getInstance().setCurrentGameState(new StartMenu());
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
