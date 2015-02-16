package com.jabyftw.gameclient.network.packets.server;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.LoginResponse;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketLoginResponse extends Packet {

    LoginResponse loginResponse;
    OnlinePlayerProfile playerProfile = null;

    public PacketLoginResponse(LoginResponse loginResponse) {
        super(PacketType.LOGIN_RESPONSE);
        this.loginResponse = loginResponse;
    }

    public PacketLoginResponse(OnlinePlayerProfile playerProfile) {
        super(PacketType.LOGIN_RESPONSE);
        this.loginResponse = LoginResponse.SUCCESSFUL_LOGIN;
        this.playerProfile = playerProfile;
    }

    public PacketLoginResponse() {
        this(PacketType.LOGIN_RESPONSE);
    }

    protected PacketLoginResponse(PacketType packetType) {
        super(packetType);
    }

    public LoginResponse getLoginResponse() {
        return loginResponse;
    }

    public OnlinePlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("loginResponse", loginResponse.ordinal(), Integer.class);
        if(playerProfile != null)
            json.writeValue("playerProfile", playerProfile, OnlinePlayerProfile.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.loginResponse = LoginResponse.valueOf(jsonData.getInt("loginResponse"));

        if(loginResponse == LoginResponse.SUCCESSFUL_LOGIN && jsonData.has("playerProfile")) {
            this.playerProfile = new OnlinePlayerProfile();
            this.playerProfile.read(json, jsonData.get("playerProfile"));
        }
    }

}
