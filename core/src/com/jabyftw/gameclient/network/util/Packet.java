package com.jabyftw.gameclient.network.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;

/**
 * Created by Rafael on 12/01/2015.
 */
public abstract class Packet implements Json.Serializable {

    private PacketType packetType;

    protected Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    @Override
    public void write(Json json) {
        json.writeValue("deliverType", packetType.getId());
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        packetType = PacketType.valueOf(jsonData.getInt("deliverType"));
    }
}
