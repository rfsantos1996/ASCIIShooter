package com.jabyftw.gameclient.maps.util;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Isa on 31/12/2014.
 */
public enum BlockOpacity {

    FULLY_VISIBLE(1, new Color(0.16f, 0.16f, 0.16f, 1)), // Don't know why, opacity doesn't work here
    //SAW_RECENTLY(0.45f, FULLY_VISIBLE.getBackgroundColor().cpy().mul(0.5f, 0.5f, 0.5f, 1)),
    //DISCOVERED(0.3f, SAW_RECENTLY.getBackgroundColor()),
    UNDISCOVERED(0, null);

    private final float opacity;
    private final Color backgroundColor;

    BlockOpacity(float opacity, Color backgroundColor) {
        this.opacity = opacity;
        this.backgroundColor = backgroundColor;
    }

    public float getOpacity() {
        return opacity;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}
