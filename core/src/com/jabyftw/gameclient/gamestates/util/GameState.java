package com.jabyftw.gameclient.gamestates.util;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.util.GameDrawable;
import com.jabyftw.gameclient.util.HudDrawable;
import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Rafael on 27/12/2014.
 */
public interface GameState extends Tickable, GameDrawable, HudDrawable, Disposable {

    public void create();

    public boolean shouldRegisterInput();

    public InputProcessor getActualInputProcessor();

    public Color getBackgroundColor();

}
