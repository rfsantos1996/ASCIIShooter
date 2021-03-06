package com.jabyftw.gameclient.network.packets.client;

import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.util.Constants;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketLoginRequest extends Packet {

    private float gameVersion = Constants.GAME_VERSION;
    String username;
    String password;

    public PacketLoginRequest(String username, String password) {
        super(PacketType.LOGIN_REQUEST);
        this.username = username;
        this.password = password;
    }

    public PacketLoginRequest() {
        this(PacketType.LOGIN_REQUEST);
    }

    protected PacketLoginRequest(PacketType packetType) {
        super(packetType);
    }

    public float getGameVersion() {
        return gameVersion;
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
        json.writeValue("gameVersion", gameVersion, Float.class);
        json.writeValue("username", username, String.class);
        json.writeValue("password", Base64Coder.encodeString(password), String.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.gameVersion = jsonData.getFloat("gameVersion");
        this.username = jsonData.getString("username");
        this.password = jsonData.getString("password");
    }
}
