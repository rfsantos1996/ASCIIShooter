package com.jabyftw.gameclient.network.packets.server;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketLoginResponse extends Packet {

    private LoginResponse loginResponse;

    public PacketLoginResponse(LoginResponse loginResponse) {
        super(PacketType.LOGIN_RESPONSE);
        this.loginResponse = loginResponse;
    }

    public PacketLoginResponse() {
        super(PacketType.LOGIN_RESPONSE);
    }

    public LoginResponse getLoginResponse() {
        return loginResponse;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("loginResponse", loginResponse.ordinal());
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.loginResponse = LoginResponse.valueOf(jsonData.getInt("loginResponse"));
    }

    public enum LoginResponse {

        SUCCESSFUL_LOGIN(LangEnum.SUCCESSFUL_LOGIN_RESPONSE),

        INVALID_USERNAME(LangEnum.INVALID_USERNAME_RESPONSE),
        USERNAME_TOO_SHORT(LangEnum.USERNAME_TOO_SHORT),
        USERNAME_TOO_LONG(LangEnum.USERNAME_TOO_LONG),

        WRONG_PASSWORD(LangEnum.WRONG_PASSWORD_RESPONSE),
        PASSWORD_TOO_SHORT(LangEnum.USERNAME_TOO_SHORT),
        PASSWORD_TOO_LONG(LangEnum.USERNAME_TOO_LONG),

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
}
