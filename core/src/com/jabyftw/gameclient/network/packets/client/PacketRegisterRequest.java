package com.jabyftw.gameclient.network.packets.client;

import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.Packet;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketRegisterRequest extends Packet {

    private String username, password;

    public PacketRegisterRequest(String username, String password) {
        super(PacketType.REGISTER_REQUEST);
        this.username = username;
        this.password = password;
    }

    public PacketRegisterRequest() {
        super(PacketType.REGISTER_REQUEST);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("username", username, String.class);
        json.writeValue("password", Base64Coder.encodeString(password), String.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.username = jsonData.getString("username");
        this.password = jsonData.getString("password");
    }
}