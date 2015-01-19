package com.jabyftw.gameclient.util.files.enums;

/**
 * Created by Rafael on 15/12/2014.
 */
public enum FontEnum {

    PRESS_START_46("fonts/PressStart2P.ttf", 46),
    PRESS_START_28("fonts/PressStart2P.ttf", 28),
    PRESS_START_20("fonts/PressStart2P.ttf", 20),
    PRESS_START_14("fonts/PressStart2P.ttf", 14),
    PRESS_START_10("fonts/PressStart2P.ttf", 10);

    private final String filePath;
    private final int size;

    private FontEnum(String filePath, int size) {
        this.filePath = filePath;
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getSize() {
        return size;
    }
}
