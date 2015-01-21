package com.jabyftw.gameclient.gamestates.mapeditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.StartMenu;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.screen.Button;
import com.jabyftw.gameclient.screen.ButtonTable;
import com.jabyftw.gameclient.screen.TextInputButton;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Isa on 01/01/2015.
 */
public class MapEditorPreparationState extends TabledGameState {

    public static final int MINIMUM_WIDTH = MathUtils.ceilPositive(Main.V_WIDTH / Map.TILE_SCALE_WIDTH),
            MINIMUM_HEIGHT = MathUtils.ceilPositive(Main.V_HEIGHT / Map.TILE_SCALE_HEIGHT);
    public static final int MAXIMUM_WIDTH = MathUtils.floorPositive(25f * (float) MINIMUM_WIDTH),
            MAXIMUM_HEIGHT = MathUtils.floorPositive(25f * (float) MINIMUM_HEIGHT);

    private TextInputButton mapNameInput;
    protected Map map;

    public MapEditorPreparationState() {
        super(false);
    }

    @Override
    public void create() {
        map = new Map();

        map.setWidth(MINIMUM_WIDTH);
        map.setHeight(MINIMUM_HEIGHT);

        FontEnum font = FontEnum.PRESS_START_28;

        gameStateTitleFont = font;
        gameStateTitle = Resources.getLang(LangEnum.MAP_EDITOR_PREFERENCES);

        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getBitmapFont(FontEnum.PRESS_START_14), Resources.getLang(LangEnum.WIDTH_FOR_MAP), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(
                            getText().replaceAll("%width%", String.valueOf(map.getWidth()))
                                    .replaceAll("%ratio%", Util.formatDecimal((map.getWidth() * Map.TILE_SCALE_WIDTH) / Main.V_WIDTH, 1))
                    );
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    map.setWidth(map.getWidth() + Util.getButtonTimesPressedValue(positiveAction, timesPressed));
                    if(map.getWidth() < MINIMUM_WIDTH) map.setWidth(MINIMUM_WIDTH);
                    else if(map.getWidth() > MAXIMUM_WIDTH) map.setWidth(MAXIMUM_WIDTH);
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getBitmapFont(FontEnum.PRESS_START_14), Resources.getLang(LangEnum.HEIGHT_FOR_MAP), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(
                            getText().replaceAll("%height%", String.valueOf(map.getHeight()))
                                    .replaceAll("%ratio%", Util.formatDecimal((map.getHeight() * Map.TILE_SCALE_HEIGHT) / Main.V_HEIGHT, 1))
                    );
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    map.setHeight(map.getHeight() + Util.getButtonTimesPressedValue(positiveAction, timesPressed));
                    if(map.getHeight() < MINIMUM_HEIGHT) map.setHeight(MINIMUM_HEIGHT);
                    else if(map.getHeight() > MAXIMUM_HEIGHT) map.setHeight(MAXIMUM_HEIGHT);
                }
            });
        }
        {
            mapNameInput = new TextInputButton(Resources.getBitmapFont(FontEnum.PRESS_START_14), Resources.getLang(LangEnum.DISPLAY_NAME_FOR_MAP)) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    super.update(deltaTime, isSelected);
                }
            };
            buttonTable.addButton(mapNameInput);
        }
        {
            buttonTable.addButton(new Button(Resources.getLang(LangEnum.ENTER_BUTTON), false) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    Array<String> valid = isValid();
                    if(valid.size == 0) {
                        map.setDisplayName(mapNameInput.getInput());
                        map.createBaseBlock();
                        map.setShouldDispose(false);
                        Main.getInstance().setCurrentGameState(new MapEditorState(map));
                    } else {
                        setDisplayText(getText() + Resources.getLang(LangEnum.FAILED_ENTER_BUTTON).replaceAll("%fail%", valid.get(0)));
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
                    Main.getInstance().setCurrentGameState(new StartMenu());
                }
            });
        }

        super.create();
    }

    @Override
    public void dispose() {
        if(map.shouldDispose())
            map.dispose();
        super.dispose();
    }

    private Array<String> isValid() {
        Array<String> failedStuff = new Array<String>();
        if(mapNameInput.getInput().length() <= 3)
            failedStuff.add(Resources.getLang(LangEnum.MAP_NAME_TOO_SHORT));
        if(mapNameInput.getInput().length() > 16)
            failedStuff.add(Resources.getLang(LangEnum.MAP_NAME_TOO_LONG));
        return failedStuff;
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }
}
