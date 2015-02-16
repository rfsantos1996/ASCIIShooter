package com.jabyftw.gameclient.util.files;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.util.Constants;

/**
 * Created by Rafael on 12/01/2015.
 */
public class OnlinePlayerProfile implements Json.Serializable {

    // Version 1.0
    private final Layout[] layouts;
    private String playerName;
    private int level = Constants.Gameplay.Player.DEFAULT_PROFILE_LEVEL;
    private float exp = 0;

    public OnlinePlayerProfile() {
        this.layouts = new Layout[Constants.Gameplay.Player.NUMBER_OF_LAYOUTS];
        for(int i = 0; i < layouts.length; i++) {
            (layouts[i] = new Layout(i)).validate(this);
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.min(level, Constants.Gameplay.Player.MAXIMUM_PROFILE_LEVEL);
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
        json.writeValue("profileVersion", Constants.ONLINE_PROFILE_VERSION, Float.class);
        {
            // Version 1.0
            json.writeValue("playerName", playerName, String.class);
            json.writeValue("level", level, Float.class);
            json.writeValue("exp", exp, Float.class);
            json.writeArrayStart("layouts");
            for(Layout layout : layouts) {
                json.writeValue(layout, Layout.class);
            }
            json.writeArrayEnd();
        }
        //System.out.println("OnlinePlayerProfile.write = { level: " + level + " exp: " + exp + " }");
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        float profileVersion = jsonData.getFloat("profileVersion");
        {
            // Version 1.0
            this.playerName = jsonData.getString("playerName");
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
        System.out.println("OnlinePlayerProfile.read = { playerName: " + playerName + " level: " + level + " exp: " + exp + " }");
    }
}
