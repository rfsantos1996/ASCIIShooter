package com.jabyftw.gameclient.util.files.enums;

import com.badlogic.gdx.Files;

/**
 * Created by Rafael on 07/01/2015.
 */
public enum FilesEnum {

    PLAYER_PROFILE_FILE(Files.FileType.Local, "playerProfile.prof"),
    LOCAL_MAP_DIRECTORY(Files.FileType.Local, "maps/", true),
    LANGUAGE_DIRECTORY(Files.FileType.Local, "lang/", true);

    private final Files.FileType type;
    private final String path;
    private final boolean shouldExist;

    private FilesEnum(Files.FileType type, String path) {
        this(type, path, false);
    }

    private FilesEnum(Files.FileType type, String path, boolean shouldExist) {
        this.type = type;
        this.path = path;
        this.shouldExist = shouldExist;
    }

    public Files.FileType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public boolean shouldExist() {
        return shouldExist;
    }
}
