package com.jabyftw.gameclient.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.util.PseudoGameState;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 17/12/2014.
 */
public class ConfigMenu extends TabledGameState implements PseudoGameState {

    public ConfigMenu() {
        super(true);
    }

    @Override
    public void create() {
        gameStateTitle = Resources.getLang(LangEnum.SETTINGS_BUTTON);
        buttonTable = new ButtonTable(FontEnum.PRESS_START_28, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.SELECTED_LANGUAGE_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(getText().replaceAll("%selected%", Main.getOfflineProfile().getSelectedLanguage().getDisplayName()));
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Resources.loadLanguage(Resources.Language.getFromOrdinal(
                            Main.getOfflineProfile().getSelectedLanguage().ordinal() + (positiveAction ? 1 : -1)
                    ));
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.SHADOW_OFFSET_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(getText().replaceAll("%shadowoffset%", String.valueOf(Util.TEXT_SHADOW_OFFSET_SIZE)));
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Util.TEXT_SHADOW_OFFSET_SIZE += (positiveAction ? 1 : -1);
                    if(Util.TEXT_SHADOW_OFFSET_SIZE <= 0) Util.TEXT_SHADOW_OFFSET_SIZE = 0;
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.ENTER_FULLSCREEN), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    if(Gdx.graphics.isFullscreen())
                        setDisplayText(getText().replace(Resources.getLang(LangEnum.ENTER_FULLSCREEN), Resources.getLang(LangEnum.LEAVE_FULLSCREEN)));
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    if(Gdx.graphics.isFullscreen())
                        Gdx.graphics.setDisplayMode(Main.V_WIDTH, Main.V_HEIGHT, false);
                    else
                        Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
                }
            });
            buttonTable.addButton(new Button(Resources.getBitmapFont(FontEnum.PRESS_START_20), Resources.getLang(LangEnum.RESTORE_DEFAULT_SCREEN_SIZE), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Gdx.graphics.setDisplayMode(Main.V_WIDTH, Main.V_HEIGHT, Gdx.graphics.isFullscreen());
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
