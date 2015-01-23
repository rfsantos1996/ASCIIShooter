package com.jabyftw.gameclient.gamestates.play;

import com.badlogic.gdx.graphics.Color;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.play.other.CreateLayoutMenu;
import com.jabyftw.gameclient.gamestates.play.other.CreateMatchMenu;
import com.jabyftw.gameclient.gamestates.play.other.FindMatchMenu;
import com.jabyftw.gameclient.gamestates.util.PseudoGameState;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 22/01/2015.
 */
public class PlayOptionsMenu extends TabledGameState implements PseudoGameState {

    public PlayOptionsMenu() {
        super(true);
    }

    @Override
    public void create() {
        FontEnum font = FontEnum.PRESS_START_20;

        gameStateTitleString = Resources.getLang(LangEnum.PLAY_BUTTON);
        gameStateTitleFont = FontEnum.PRESS_START_28;

        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.FIND_MATCH_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(new FindMatchMenu());
                }
            });
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.CREATE_MATCH_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(new CreateMatchMenu());
                }
            });
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.CHANGE_LAYOUTS_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(new CreateLayoutMenu());
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.BACK_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.getInstance().setCurrentGameState(null);
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
