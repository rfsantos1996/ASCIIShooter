package com.jabyftw.gameclient.network.packets.server;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.util.Util;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketPingResponse extends Packet {

    private long responseTime;

    public PacketPingResponse(long pingRequestTime) {
        super(PacketType.PING_RESPONSE);
        responseTime = Util.getUTCTime() - pingRequestTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("responseTime", responseTime, Long.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        responseTime = jsonData.getLong("responseTime");
    }
}
