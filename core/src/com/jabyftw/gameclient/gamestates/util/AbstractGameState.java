package com.jabyftw.gameclient.gamestates.util;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Rafael on 05/12/2014.
 */
public abstract class AbstractGameState implements GameState {

    protected InputAdapter desktopProcessor;

    public InputProcessor getActualInputProcessor() {
        return desktopProcessor;
    }

    @Override
    public Color getBackgroundColor() {
        return new Color(0.43f, 0.43f, 0.43f, 1);
    }
}
