package com.jabyftw.gameclient.network.packets.client;

import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketRegisterRequest extends PacketLoginRequest {

    private String secondPassword;

    public PacketRegisterRequest(String username, String password, String secondPassword) {
        this();
        this.username = username;
        this.password = password;
        this.secondPassword = secondPassword;
    }

    public PacketRegisterRequest() {
        super(PacketType.REGISTER_REQUEST);
    }

    public String getSecondPassword() {
        return secondPassword;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("username", username, String.class);
        json.writeValue("password", Base64Coder.encodeString(password), String.class);
        json.writeValue("secondPassword", Base64Coder.encodeString(secondPassword), String.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.username = jsonData.getString("username");
        this.password = jsonData.getString("password");
        this.secondPassword = jsonData.getString("secondPassword");
    }
}
