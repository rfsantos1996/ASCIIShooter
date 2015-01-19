package com.jabyftw.gameclient.network.packets.server;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketRegisterResponse extends Packet {

    private RegisterResponse registerResponse;

    public PacketRegisterResponse(RegisterResponse registerResponse) {
        this();
        this.registerResponse = registerResponse;
    }

    public PacketRegisterResponse() {
        super(PacketType.REGISTER_RESPONSE);
    }

    public RegisterResponse getRegisterResponse() {
        return registerResponse;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("registerResponse", registerResponse.ordinal());
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.registerResponse = RegisterResponse.valueOf(jsonData.getInt("registerResponse"));
    }

    public enum RegisterResponse {

        SUCCESSFUL_REGISTER(LangEnum.SUCCESSFUL_LOGIN_RESPONSE),

        USERNAME_ALREADY_TAKEN(LangEnum.USERNAME_ALREADY_TAKEN),
        USERNAME_TOO_LONG(LangEnum.USERNAME_TOO_LONG),
        USERNAME_TOO_SHORT(LangEnum.USERNAME_TOO_SHORT),

        PASSWORD_TOO_SHORT(LangEnum.PASSWORD_TOO_SHORT),
        PASSWORD_TOO_LONG(LangEnum.PASSWORD_TOO_LONG),

        UNKNOWN_ERROR(LangEnum.UNKNOWN_ERROR);

        private final LangEnum langEnum;

        private RegisterResponse(LangEnum langEnum) {
            this.langEnum = langEnum;
        }

        public LangEnum getLangEnum() {
            return langEnum;
        }

        public int getId() {
            return ordinal();
        }

        public static RegisterResponse valueOf(int ordinal) {
            return values()[ordinal];
        }
    }
}
