package com.jabyftw.gameclient.gamestates.play;

import com.badlogic.gdx.graphics.Color;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.entities.PlayerEntity;
import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.gamestates.util.PseudoGameState;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Isa on 04/01/2015.
 */
public class ChangeLayoutMenu extends TabledGameState implements PseudoGameState {

    private final PlayState playState;
    private int selectedLayout;

    public ChangeLayoutMenu(PlayState playState) {
        super(true);
        this.playState = playState;
    }

    @Override
    public void create() {
        FontEnum font = FontEnum.PRESS_START_28;

        gameStateTitleFont = font;
        gameStateTitle = Resources.getLang(LangEnum.CHANGE_LAYOUT_TITLE);
        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));

        final PlayerEntity playerEntity = playState.getPlayerEntity();
        this.selectedLayout = playerEntity.getSelectedLayout();
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.SELECT_LAYOUT_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(getText().replaceAll("%layoutname%", Main.getOnlineProfile().getLayouts()[selectedLayout].getDisplayName()));
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    selectedLayout += (positiveAction ? 1 : -1);

                    Layout[] layouts = Main.getOnlineProfile().getLayouts();
                    if(selectedLayout >= layouts.length)
                        selectedLayout = 0;
                    else if(selectedLayout < 0)
                        selectedLayout = layouts.length - 1;
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.ENTER_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    playerEntity.setSelectedLayout(selectedLayout);
                    playerEntity.doDamage(playerEntity.getMaximumHealth());
                    Main.getInstance().setCurrentGameState(null);
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
        super.create();
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }
}
