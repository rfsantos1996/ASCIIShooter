package com.jabyftw.gameclient.network.util;

import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
* Created by Rafael on 07/02/2015.
*/
public enum LoginResponse {

    SUCCESSFUL_LOGIN(LangEnum.SUCCESSFUL_LOGIN_RESPONSE),
    //SUCCESSFUL_REGISTER(LangEnum.SUCCESSFUL_LOGIN_RESPONSE),

    USERNAME_BANNED(LangEnum.PLAYER_BANNED_RESPONSE),
    USERNAME_ALREADY_TAKEN(LangEnum.USERNAME_ALREADY_TAKEN),
    USERNAME_ALREADY_LOGGED_IN(LangEnum.USERNAME_LOGGED_IN_RESPONSE),
    USERNAME_INVALID(LangEnum.INVALID_USERNAME_RESPONSE),

    USERNAME_TOO_SHORT(LangEnum.USERNAME_TOO_SHORT),
    USERNAME_TOO_LONG(LangEnum.USERNAME_TOO_LONG),

    WRONG_PASSWORD(LangEnum.WRONG_PASSWORD_RESPONSE),
    PASSWORD_MISMATCHING(LangEnum.PASSWORD_MISMATCHING_RESPONSE),
    PASSWORD_TOO_SHORT(LangEnum.USERNAME_TOO_SHORT),
    PASSWORD_TOO_LONG(LangEnum.USERNAME_TOO_LONG),

    SERVER_LOADED(LangEnum.SERVER_LOADED_RESPONSE),
    UNKNOWN_ERROR(LangEnum.UNKNOWN_ERROR);

    private final LangEnum langEnum;

    private LoginResponse(LangEnum langEnum) {
        this.langEnum = langEnum;
    }

    public LangEnum getLangEnum() {
        return langEnum;
    }

    public int getId() {
        return ordinal();
    }

    public static LoginResponse valueOf(int ordinal) {
        return values()[ordinal];
    }
}
