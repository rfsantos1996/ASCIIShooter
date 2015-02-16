package com.jabyftw.gameclient.util.files;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.enums.FilesEnum;
import com.sun.istack.internal.NotNull;

/**
 * Created by Isa on 05/01/2015.
 */
public class OfflinePlayerProfile implements Json.Serializable {

    /*
     * VERSION 1.0
     */
    // Profile stuff
    private boolean newProfile = true;
    private String playerName = "";
    // Screen
    private int width = Constants.Display.V_WIDTH, height = Constants.Display.V_HEIGHT;
    private boolean fullscreen = false;
    // Other
    private int lastSelectedLayout = 0;
    private int textShadowOffset = Constants.Display.DEFAULT_TEXT_SHADOW_OFFSET;
    private Resources.Language selectedLanguage = Resources.Language.ENGLISH;

    /*
     * CONSTRUCTOR
     */
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

    public int getLastSelectedLayout() {
        return lastSelectedLayout;
    }

    public void setLastSelectedLayout(int lastSelectedLayout) {
        this.lastSelectedLayout = lastSelectedLayout;
    }

    public int getTextShadowOffset() {
        return textShadowOffset;
    }

    public void setTextShadowOffset(int textShadowOffset) {
        this.textShadowOffset = Math.max(0, textShadowOffset);
        Util.TEXT_SHADOW_OFFSET_SIZE = this.textShadowOffset;
    }

    public Resources.Language getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(@NotNull Resources.Language selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public boolean isNewProfile() {
        return newProfile;
    }

    /*
     * JSON READERS/WRITERS
     */

    @Override
    public void write(Json json) {
        json.writeValue("profileVersion", Constants.OFFLINE_PROFILE_VERSION, Float.class);
        { // Version 1.0
            // Player stuff
            json.writeValue("playerName", playerName, String.class);
            // Screen
            json.writeValue("width", width, Integer.class);
            json.writeValue("height", height, Integer.class);
            json.writeValue("fullscreen", fullscreen, Boolean.class);
            // Other
            json.writeValue("lastSelectedLayout", lastSelectedLayout, Integer.class);
            json.writeValue("textShadowOffset", textShadowOffset, Integer.class);
            json.writeValue("selectedLanguage", selectedLanguage.name(), String.class);
        }
        System.out.println("OfflinePlayerProfile.write = { name: " + playerName + " selectedLanguage: " + selectedLanguage.name() + " }");
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.newProfile = false;

        float profileVersion = jsonData.getFloat("profileVersion");
        { // Version 1.0
            // Player name
            setPlayerName(jsonData.getString("playerName"));
            // Screen
            setDisplayMode(jsonData.getInt("width"), jsonData.getInt("height"), jsonData.getBoolean("fullscreen"));
            // Other
            setLastSelectedLayout(jsonData.getInt("lastSelectedLayout"));
            setTextShadowOffset(jsonData.getInt("textShadowOffset"));
            setSelectedLanguage(Resources.Language.valueOf(jsonData.getString("selectedLanguage")));
        }
        System.out.println("OfflinePlayerProfile.read = { name: " + playerName + " selectedLanguage: " + selectedLanguage.name() + " }");
    }

    public void saveProfile() {
        this.newProfile = false;
        Resources.getFileHandle(FilesEnum.PLAYER_PROFILE_FILE).writeString(Base64Coder.encodeString(new Json().toJson(this)), false);
    }

    public static OfflinePlayerProfile readProfile(FileHandle fileHandle) {
        return new Json().fromJson(OfflinePlayerProfile.class, Base64Coder.decodeString(fileHandle.readString()));
    }
}
