package com.jabyftw.gameclient.network.packets;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.Packet;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketKeepAwake extends Packet {

    public PacketKeepAwake() {
        super(PacketType.KEEP_AWAKE);
    }

    @Override
    public void write(Json json) {
        super.write(json);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
    }
}
