package com.jabyftw.gameclient.network.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;

/**
 * Created by Rafael on 12/01/2015.
 */
public class Packet implements Json.Serializable {

    protected PacketType packetType;

    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    @Override
    public void write(Json json) {
        json.writeValue("type", packetType.getId());
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        packetType = PacketType.valueOf(jsonData.getInt("type"));
    }
}
