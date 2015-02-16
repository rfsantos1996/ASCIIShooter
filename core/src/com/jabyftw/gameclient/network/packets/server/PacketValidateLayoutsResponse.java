package com.jabyftw.gameclient.network.packets.server;

import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.client.PacketValidateLayoutsRequest;

/**
 * Created by Rafael on 11/02/2015.
 */
public class PacketValidateLayoutsResponse extends PacketValidateLayoutsRequest {

    public PacketValidateLayoutsResponse() {
        super(PacketType.VALIDATE_LAYOUT_RESPONSE);
    }

    public PacketValidateLayoutsResponse(Layout[] layouts) {
        super(PacketType.VALIDATE_LAYOUT_RESPONSE, layouts);
    }
}
