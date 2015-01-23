package com.jabyftw.gameclient.util.files;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.entity.weapon.Layout;

/**
 * Created by Rafael on 12/01/2015.
 */
public class OnlinePlayerProfile implements Json.Serializable {

    public static final float PROFILE_VERSION = 0.1f; // Used server-wide for saving and stuff
    public static final int MAXIMUM_LEVEL = 15;

    // Version 1.0
    private int level;
    private float exp;
    private Layout[] layouts;

    public OnlinePlayerProfile() {
        this.level = 15;

        this.layouts = new Layout[5];
        for(int i = 0; i < layouts.length; i++) {
            layouts[i] = new Layout("Layout " + (i + 1));
            layouts[i].validate(this);
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.min(level, MAXIMUM_LEVEL);
    }

    public float getExp() {
        return exp;
    }

    public void setExp(float exp) {
        this.exp = exp;
    }

    public Layout[] getLayouts() {
        return layouts;
    }

    /*
     * WRITE AND READ
     */

    @Override
    public void write(Json json) {
        json.writeValue("profileVersion", PROFILE_VERSION, Float.class);
        {
            json.writeValue("level", level, Float.class);
            json.writeValue("exp", exp, Float.class);
            json.writeArrayStart("layouts");
            for(Layout layout : layouts) {
                json.writeValue(layout, Layout.class);
            }
            json.writeArrayEnd();
        }
        System.out.println("OnlinePlayerProfile.write = { level: " + level + " exp: " + exp + " }");
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        float profileVersion = jsonData.getFloat("profileVersion");
        {
            // Version 1.0
            this.level = jsonData.getInt("level");
            this.exp = jsonData.getFloat("exp");
            {
                // Read layouts
                JsonValue layouts = jsonData.get("layouts");

                int index = 0;
                JsonValue next;
                while((next = layouts.get(index)) != null) {
                    Layout layout = new Layout();
                    layout.read(json, next);
                    this.layouts[index++] = layout;
                }
            }
        }
        System.out.println("OnlinePlayerProfile.read = { level: " + level + " exp: " + exp + " }");
    }
}
