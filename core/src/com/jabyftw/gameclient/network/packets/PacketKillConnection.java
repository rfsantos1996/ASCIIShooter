package com.jabyftw.gameclient.network.packets;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.network.PacketType;

/**
 * Created by Rafael on 12/01/2015.
 */
public class PacketKillConnection extends Packet {

    public PacketKillConnection() {
        super(PacketType.KILL_CONNECTION);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
    }

    @Override
    public void write(Json json) {
        super.write(json);
    }
}
