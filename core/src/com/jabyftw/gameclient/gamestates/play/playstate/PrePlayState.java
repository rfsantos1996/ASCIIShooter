package com.jabyftw.gameclient.gamestates.play.playstate;

import com.badlogic.gdx.graphics.Color;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.util.PseudoGameState;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

import java.util.Set;

/**
 * Created by Rafael on 07/01/2015.
 */
public class PrePlayState extends TabledGameState implements PseudoGameState {

    private String[] availableMaps;
    private int selectedMap = 0;

    public PrePlayState() {
        super(true);
    }

    @Override
    public void create() {
        FontEnum font = FontEnum.PRESS_START_28;

        Set<String> mapNames = Resources.getMapNames();
        availableMaps = mapNames.toArray(new String[mapNames.size()]);

        gameStateTitleString = Resources.getLang(LangEnum.SELECT_MAP_TITLE);
        gameStateTitleFont = font;

        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        if(!mapNames.isEmpty()) {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.SELECT_MAP_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(getText().replaceAll("%mapname%", availableMaps[selectedMap]));
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    selectedMap += (positiveAction ? 1 : -1);

                    if(selectedMap >= availableMaps.length)
                        selectedMap = 0;
                    else if(selectedMap < 0)
                        selectedMap = availableMaps.length - 1;
                }
            });
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.ENTER_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.setCurrentGameState(new PlayState(Resources.loadFinishedMap(availableMaps[selectedMap]), false));
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(mapNames.isEmpty() ? LangEnum.NO_MAPS_AVAILABLE : LangEnum.BACK_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.setCurrentGameState(null);
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
