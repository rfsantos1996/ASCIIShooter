package com.jabyftw.gameclient.util.files;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.entity.weapon.Layout;

/**
 * Created by Rafael on 12/01/2015.
 */
public class OnlinePlayerProfile implements Json.Serializable {

    public static final int MAXIMUM_LEVEL = 15;

    private int level;
    private float exp;

    private Layout[] layouts = new Layout[5];

    public OnlinePlayerProfile() {
        level = 15;
        for(int i = 0; i < layouts.length; i++) {
            layouts[i] = new Layout("Layout " + (i + 1));
            layouts[i].validate(this);
        }
    }

    public Layout[] getLayouts() {
        return layouts;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void write(Json json) {
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
    }
}
