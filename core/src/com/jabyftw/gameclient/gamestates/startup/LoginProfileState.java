package com.jabyftw.gameclient.gamestates.startup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.screen.TextInputButton;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 08/01/2015.
 */
public class LoginProfileState extends TabledGameState {

    private TextInputButton playerNameInput, passwordInput;
    private boolean typedAnything = false;

    public LoginProfileState() {
        super(false);
    }

    @Override
    public void create() {
        final FontEnum font = FontEnum.PRESS_START_28;

        gameStateTitleString = Resources.getLang(LangEnum.LOGIN_STATE_TITLE);
        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        if(Main.getOfflineProfile().isNewProfile()) {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.SELECTED_LANGUAGE_BUTTON), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(getText().replaceAll("%selected%", Main.getOfflineProfile().getSelectedLanguage().getDisplayName()));
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Resources.loadLanguage(Resources.Language.getFromOrdinal(Main.getOfflineProfile().getSelectedLanguage().ordinal() + (positiveAction ? 1 : -1)), false);
                }
            });
        }
        {
            playerNameInput = new TextInputButton(Resources.getBitmapFont(FontEnum.PRESS_START_20), Resources.getLang(LangEnum.USERNAME_INPUT_BUTTON)) {
                @Override
                public void setInput() {
                    this.input = (Main.getOfflineProfile().isNewProfile() ? "" : Main.getOfflineProfile().getPlayerName());
                }

                @Override
                public void keyTyped(char character) {
                    super.keyTyped(character);
                    typedAnything = true;
                }
            };

            passwordInput = new TextInputButton(Resources.getBitmapFont(FontEnum.PRESS_START_20), Resources.getLang(LangEnum.PASSWORD_INPUT_BUTTON)) {
                @Override
                public void setInput() {
                    this.hideInput = true;
                }

                @Override
                public void keyTyped(char character) {
                    super.keyTyped(character);
                    typedAnything = true;
                }
            };

            buttonTable.addButton(playerNameInput);
            buttonTable.addButton(passwordInput);
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.ENTER_BUTTON), false) {

                private final Array<String> failedStuff = new Array<String>();

                @Override
                public void update(float deltaTime, boolean isSelected) {
                    if(typedAnything) {
                        isClientSideValid(failedStuff);
                        if(failedStuff.size > 0) {
                            setFont(Resources.getBitmapFont(FontEnum.PRESS_START_14));
                            setDisplayText(getText() + Resources.getLang(LangEnum.FAILED_ENTER_BUTTON).replaceAll("%fail%", failedStuff.get(0)));
                        } else {
                            setDisplayText(getText());
                            setFont(Resources.getBitmapFont(font));
                        }
                    }
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    isClientSideValid(failedStuff);
                    if(failedStuff.size == 0) {
                        setFont(Resources.getBitmapFont(font));
                        setDisplayText(getText());
                        Main.setCurrentGameState(new WaitingLoginState(playerNameInput.getInput(), passwordInput.getInput()));
                    } else {
                        setFont(Resources.getBitmapFont(FontEnum.PRESS_START_14));
                        setDisplayText(getText() + Resources.getLang(LangEnum.FAILED_ENTER_BUTTON).replaceAll("%fail%", failedStuff.get(0)));
                    }
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.REGISTER_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Main.setCurrentGameState(new RegisterProfileState(playerNameInput.getInput(), passwordInput.getInput()));
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
                    Gdx.app.exit();
                }
            });
        }
        super.create();
    }

    private void isClientSideValid(Array<String> failedStuff) {
        failedStuff.clear();

        if(playerNameInput.getInput().length() <= Constants.Gameplay.TEXT_TOO_SHORT)
            failedStuff.add(Resources.getLang(LangEnum.USERNAME_TOO_SHORT));
        if(playerNameInput.getInput().length() > Constants.Gameplay.TEXT_TOO_LONG)
            failedStuff.add(Resources.getLang(LangEnum.USERNAME_TOO_LONG));

        if(passwordInput.getInput().length() <= Constants.Gameplay.TEXT_TOO_SHORT)
            failedStuff.add(Resources.getLang(LangEnum.PASSWORD_TOO_SHORT));
        if(passwordInput.getInput().length() > Constants.Gameplay.TEXT_TOO_LONG)
            failedStuff.add(Resources.getLang(LangEnum.PASSWORD_TOO_LONG));
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }
}
