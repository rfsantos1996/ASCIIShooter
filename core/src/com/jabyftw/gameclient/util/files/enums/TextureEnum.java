package com.jabyftw.gameclient.util.files.enums;

/**
 * Created by Rafael on 15/12/2014.
 */
public enum TextureEnum {

    MATERIALS_TEXTURE("images/materials.png"),
    DOOR_TEXTURE("images/doors.png"),
    WATER_TEXTURE("images/water.png"),
    PLAYER_TEXTURE("images/player.png"),
    TARGET_TEXTURE("images/target.png");

    private final String filePath;

    private TextureEnum(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
