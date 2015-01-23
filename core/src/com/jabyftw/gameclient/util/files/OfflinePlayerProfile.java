package com.jabyftw.gameclient.util.files;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.Main;

/**
 * Created by Isa on 05/01/2015.
 */
public class OfflinePlayerProfile implements Json.Serializable {

    public static final float PROFILE_VERSION = 0.1f;

    // Version 1.0
    private String playerName;
    private int width = Main.V_WIDTH, height = Main.V_HEIGHT;
    private boolean fullscreen = false;

    private Resources.Language selectedLanguage = Resources.Language.ENGLISH;

    public OfflinePlayerProfile() {
        selectedLanguage = Resources.Language.ENGLISH;
    }

    /*
     * GETTERS & SETTERS
     */

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setDisplayMode(int width, int height, boolean fullscreen) {
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;
    }

    public Resources.Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(Resources.Language selectedLanguage) {
        if(selectedLanguage != null)
            this.selectedLanguage = selectedLanguage;
    }

    /*
     * JSON READERS/WRITERS
     */

    @Override
    public void write(Json json) {
        json.writeValue("profileVersion", PROFILE_VERSION, Float.class);
        {
            json.writeValue("playerName", playerName, String.class);
            json.writeValue("width", width, Integer.class);
            json.writeValue("height", height, Integer.class);
            json.writeValue("fullscreen", fullscreen, Boolean.class);
            json.writeValue("selectedLanguage", selectedLanguage.name(), String.class);
        }
        System.out.println("OfflinePlayerProfile.write = { name: " + playerName + " selectedLanguage: " + selectedLanguage.name() + " }");
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        float profileVersion = jsonData.getFloat("profileVersion");
        {
            // Version 1.0
            setPlayerName(jsonData.getString("playerName"));
            setDisplayMode(jsonData.getInt("width"), jsonData.getInt("height"), jsonData.getBoolean("fullscreen"));
            setSelectedLanguage(Resources.Language.valueOf(jsonData.getString("selectedLanguage")));
        }
        System.out.println("OfflinePlayerProfile.read = { name: " + playerName + " selectedLanguage: " + selectedLanguage.name() + " }");
    }

    public void saveProfile(FileHandle fileHandle) {
        fileHandle.writeString(Base64Coder.encodeString(new Json().toJson(this)), false);
    }

    public static OfflinePlayerProfile readProfile(FileHandle fileHandle) {
        return new Json().fromJson(OfflinePlayerProfile.class, Base64Coder.decodeString(fileHandle.readString()));
    }
}
