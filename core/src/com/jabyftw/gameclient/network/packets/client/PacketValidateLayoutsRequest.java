package com.jabyftw.gameclient.network.packets.client;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.Packet;

/**
 * Created by Rafael on 11/02/2015.
 */
public class PacketValidateLayoutsRequest extends Packet {

    private Layout[] layouts;

    public PacketValidateLayoutsRequest(Layout[] layouts) {
        this(PacketType.VALIDATE_LAYOUT_REQUEST, layouts);
    }

    protected PacketValidateLayoutsRequest(PacketType packetType, Layout[] layouts) {
        this(packetType);
        this.layouts = layouts;
    }

    public PacketValidateLayoutsRequest() {
        this(PacketType.VALIDATE_LAYOUT_REQUEST);
    }

    protected PacketValidateLayoutsRequest(PacketType packetType) {
        super(packetType);
    }

    public Layout[] getLayouts() {
        return layouts;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeArrayStart("layouts");
        for(Layout layout : layouts) {
            json.writeValue(layout, Layout.class);
        }
        json.writeArrayEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        JsonValue layouts = jsonData.get("layouts");
        this.layouts = new Layout[layouts.size];

        int index = 0;
        JsonValue next;
        while((next = layouts.get(index)) != null) {
            Layout layout = new Layout();
            {
                layout.read(json, next);
                this.layouts[layout.getIndex()] = layout;
            }
            index++;
        }
    }
}
