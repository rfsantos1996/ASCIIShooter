package com.jabyftw.gameclient.gamestates.startup;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.util.PseudoGameState;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.screen.TextInputButton;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 07/02/2015.
 */
public class RegisterProfileState extends TabledGameState implements PseudoGameState {

    private final String playerName, password;
    private TextInputButton playerNameInput, passwordInput_first, passwordInput_second;

    private final Array<String> failedStuff = new Array<String>();
    private boolean typedAnything = false;

    public RegisterProfileState(String playerName, String password) {
        super(true);
        this.playerName = playerName;
        this.password = password;
    }

    @Override
    public void create() {
        final FontEnum font = FontEnum.PRESS_START_28;

        gameStateTitleString = Resources.getLang(LangEnum.REGISTER_STATE_TITLE);
        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            playerNameInput = new TextInputButton(Resources.getBitmapFont(FontEnum.PRESS_START_20), Resources.getLang(LangEnum.USERNAME_INPUT_BUTTON)) {
                @Override
                public void setInput() {
                    this.input = playerName;
                }

                @Override
                public void keyTyped(char character) {
                    super.keyTyped(character);
                    typedAnything = true;
                }
            };

            passwordInput_first = new TextInputButton(Resources.getBitmapFont(FontEnum.PRESS_START_20), Resources.getLang(LangEnum.PASSWORD_INPUT_BUTTON)) {
                @Override
                public void setInput() {
                    this.input = password;
                    this.hideInput = true;
                }

                @Override
                public void keyTyped(char character) {
                    super.keyTyped(character);
                    typedAnything = true;
                }
            };
            passwordInput_second = new TextInputButton(Resources.getBitmapFont(FontEnum.PRESS_START_20), Resources.getLang(LangEnum.PASSWORD_INPUT_SECOND_BUTTON)) {
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
            buttonTable.addButton(passwordInput_first);
            buttonTable.addButton(passwordInput_second);
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.ENTER_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    if(typedAnything) {
                        isClientSideValid(failedStuff);
                        if(failedStuff.size > 0) {
                            setFont(Resources.getBitmapFont(FontEnum.PRESS_START_14));
                            setDisplayText(getText() + Resources.getLang(LangEnum.FAILED_ENTER_BUTTON).replaceAll("%fail%", failedStuff.get(0)));
                        } else {
                            setFont(Resources.getBitmapFont(font));
                            setDisplayText(getText());
                        }
                    }
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    isClientSideValid(failedStuff);
                    if(failedStuff.size == 0) {
                        setFont(Resources.getBitmapFont(font));
                        setDisplayText(getText());
                        Main.setCurrentGameState(new WaitingLoginState(playerNameInput.getInput(), passwordInput_first.getInput(), passwordInput_second.getInput()));
                    } else {
                        setFont(Resources.getBitmapFont(FontEnum.PRESS_START_14));
                        setDisplayText(getText() + Resources.getLang(LangEnum.FAILED_ENTER_BUTTON).replaceAll("%fail%", failedStuff.get(0)));
                    }
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
        super.create();
    }

    private void isClientSideValid(Array<String> failedStuff) {
        failedStuff.clear();

        if(playerNameInput.getInput().length() <= Constants.Gameplay.TEXT_TOO_SHORT)
            failedStuff.add(Resources.getLang(LangEnum.USERNAME_TOO_SHORT));
        if(playerNameInput.getInput().length() > Constants.Gameplay.TEXT_TOO_LONG)
            failedStuff.add(Resources.getLang(LangEnum.USERNAME_TOO_LONG));

        if(passwordInput_first.getInput().length() <= Constants.Gameplay.TEXT_TOO_SHORT)
            failedStuff.add(Resources.getLang(LangEnum.PASSWORD_TOO_SHORT));
        if(passwordInput_first.getInput().length() > Constants.Gameplay.TEXT_TOO_LONG)
            failedStuff.add(Resources.getLang(LangEnum.PASSWORD_TOO_LONG));

        if(passwordInput_second.getInput().length() <= Constants.Gameplay.TEXT_TOO_SHORT)
            failedStuff.add(Resources.getLang(LangEnum.PASSWORD_TOO_SHORT));
        if(passwordInput_second.getInput().length() > Constants.Gameplay.TEXT_TOO_LONG)
            failedStuff.add(Resources.getLang(LangEnum.PASSWORD_TOO_LONG));

        if(!passwordInput_first.getInput().equals(passwordInput_second.getInput()))
            failedStuff.add(Resources.getLang(LangEnum.PASSWORDS_DONT_MATCH));
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }
}
