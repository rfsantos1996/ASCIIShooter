package com.jabyftw.gameclient.gamestates.mapeditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.StartMenu;
import com.jabyftw.gameclient.gamestates.util.TabledGameState;
import com.jabyftw.gameclient.maps.Converter;
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

    public static final Vector2 minimum = Converter.SCREEN_COORDINATES.toWorldCoordinates(new Vector2(Main.V_WIDTH, Main.V_HEIGHT)),
            maximum = new Vector2(minimum).scl(25f);

    private TextInputButton mapNameInput;
    private Vector2 currentVector = new Vector2();

    protected Map map;

    public MapEditorPreparationState() {
        super(false);
    }

    @Override
    public void create() {
        map = new Map();

        map.setWidth(MathUtils.ceilPositive(minimum.x));
        map.setHeight(MathUtils.ceilPositive(minimum.y));
        updateCurrentVector();

        FontEnum font = FontEnum.PRESS_START_28;

        gameStateTitleFont = font;
        gameStateTitleString = Resources.getLang(LangEnum.MAP_EDITOR_PREFERENCES);

        buttonTable = new ButtonTable(font, new Color(1, 1, 1, 1), new Color(0.8f, 0, 0, 1));
        {
            buttonTable.addButton(new Button(Resources.getBitmapFont(FontEnum.PRESS_START_14), Resources.getLang(LangEnum.WIDTH_FOR_MAP), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(
                            getText().replaceAll("%width%", String.valueOf(map.getWidth()))
                                    .replaceAll("%ratio%", Util.formatDecimal(currentVector.x / Main.V_WIDTH, 1))
                    );
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    map.setWidth(map.getWidth() + Util.getButtonTimesPressedValue(positiveAction, timesPressed));
                    updateCurrentVector();
                    if(map.getWidth() < minimum.x) map.setWidth(MathUtils.ceilPositive(minimum.x));
                    else if(map.getWidth() > maximum.x) map.setWidth(MathUtils.ceilPositive(maximum.x));
                }
            });
        }
        {
            buttonTable.addButton(new Button(Resources.getBitmapFont(FontEnum.PRESS_START_14), Resources.getLang(LangEnum.HEIGHT_FOR_MAP), true) {
                @Override
                public void update(float deltaTime, boolean isSelected) {
                    setDisplayText(
                            getText().replaceAll("%height%", String.valueOf(map.getHeight()))
                                    .replaceAll("%ratio%", Util.formatDecimal(currentVector.y / Main.V_HEIGHT, 1))
                    );
                }

                @Override
                public void doButtonAction(boolean positiveAction, int timesPressed) {
                    map.setHeight(map.getHeight() + Util.getButtonTimesPressedValue(positiveAction, timesPressed));
                    updateCurrentVector();
                    if(map.getHeight() < minimum.y) map.setWidth(MathUtils.ceilPositive(minimum.y));
                    else if(map.getHeight() > maximum.y) map.setWidth(MathUtils.ceilPositive(maximum.y));
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

    private void updateCurrentVector() {
        currentVector.set(Converter.WORLD_COORDINATES.toScreenCoordinates(new Vector2(map.getWidth(), map.getHeight())));
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
