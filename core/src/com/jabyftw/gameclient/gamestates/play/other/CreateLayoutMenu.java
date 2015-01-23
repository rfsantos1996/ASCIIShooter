package com.jabyftw.gameclient.gamestates.play.other;

import com.badlogic.gdx.graphics.Color;
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
public class CreateLayoutMenu extends TabledGameState implements PseudoGameState {

    public CreateLayoutMenu() {
        super(true);
    }

    @Override
    public void create() {
        FontEnum font = FontEnum.PRESS_START_20;

        gameStateTitleString = Resources.getLang(LangEnum.CHANGE_LAYOUT_TITLE);
        gameStateTitleFont = FontEnum.PRESS_START_28;

        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.FIND_MATCH_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
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
