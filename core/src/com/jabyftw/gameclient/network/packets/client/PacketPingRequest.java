package com.jabyftw.gameclient.network.packets.client;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.util.Util;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketPingRequest extends Packet {

    private long requestTime;

    public PacketPingRequest() {
        super(PacketType.PING_REQUEST);
        requestTime = Util.getUTCTime();
    }

    public long getRequestTime() {
        return requestTime;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("requestTime", requestTime, Long.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        requestTime = jsonData.getLong("requestTime");
    }
}
