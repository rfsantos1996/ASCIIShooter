package com.jabyftw.gameclient.gamestates.startup;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.StartMenu;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.screen.TextInputButton;
import com.jabyftw.gameclient.util.files.OfflinePlayerProfile;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FilesEnum;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 08/01/2015.
 */
public class CreateProfileState extends TabledGameState {

    public CreateProfileState() {
        super(false);
    }

    private TextInputButton playerNameInput;

    @Override
    public void create() {
        FontEnum font = FontEnum.PRESS_START_28;

        gameStateTitleString = Resources.getLang(LangEnum.CREATE_PROFILE_TITLE);
        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
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
            playerNameInput = new TextInputButton(Resources.getBitmapFont(FontEnum.PRESS_START_14), Resources.getLang(LangEnum.PLAYER_NAME)) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    super.update(deltaTime, isSelected);
                }
            };
            buttonTable.addButton(playerNameInput);
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.ENTER_BUTTON), false) {

                private final Array<String> failedStuff = new Array<String>();

                @Override
                public void update(float deltaTime, boolean isSelected) {
                    isValid(failedStuff);
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    if(failedStuff.size == 0) {
                        OfflinePlayerProfile offlinePlayerProfile = Main.getOfflineProfile();
                        offlinePlayerProfile.setPlayerName(playerNameInput.getInput());
                        offlinePlayerProfile.saveProfile(Resources.getFileHandle(FilesEnum.PLAYER_PROFILE_FILE));
                        Main.getInstance().setCurrentGameState(new StartMenu());
                    } else {
                        setDisplayText(getText() + Resources.getLang(LangEnum.FAILED_ENTER_BUTTON).replaceAll("%fail%", failedStuff.get(0)));
                    }
                }
            });
        }
        super.create();
    }

    private Array<String> isValid(Array<String> failedStuff) {
        failedStuff.clear();
        if(playerNameInput.getInput().length() <= 3)
            failedStuff.add(Resources.getLang(LangEnum.USERNAME_TOO_SHORT));
        if(playerNameInput.getInput().length() > 16)
            failedStuff.add(Resources.getLang(LangEnum.USERNAME_TOO_LONG));
        return failedStuff;
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }
}
