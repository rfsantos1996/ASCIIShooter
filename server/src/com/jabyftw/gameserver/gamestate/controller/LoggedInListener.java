package com.jabyftw.gameserver.gamestate.controller;

import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.client.PacketValidateLayoutsRequest;
import com.jabyftw.gameclient.network.packets.server.PacketValidateLayoutsResponse;
import com.jabyftw.gameserver.network.ServerPacketHandler;

/**
 * Created by Rafael on 07/02/2015.
 */
public class LoggedInListener extends AbstractPacketListener {

    public LoggedInListener(ServerPacketHandler packetHandler, boolean registered) {
        super(packetHandler);
        validPacketTypes.add(PacketType.VALIDATE_LAYOUT_REQUEST);
        System.out.println("~ " + (registered ? "Registered" : "Logged") + " in as " + packetHandler.getConnectionName());
    }

    @Listener.PacketListener
    public void onValidationRequest(PacketValidateLayoutsRequest validateRequest) {
        Layout[] layouts = validateRequest.getLayouts();
        Array<Integer> changedIndexArray = new Array<Integer>();

        // Check what changed
        for(int i = 0; i < layouts.length; i++) {
            if(layouts[i].validate(packetHandler.getOnlinePlayerProfile()))
                changedIndexArray.add(i);

            // Update display name
            packetHandler.getOnlinePlayerProfile().getLayouts()[i].setDisplayName(layouts[i].getDisplayName());
        }

        Layout[] changedLayouts = new Layout[changedIndexArray.size];
        {
            int lastIndex = 0;
            for(Integer layoutIndex : changedIndexArray) {
                changedLayouts[lastIndex++] = layouts[layoutIndex];
            }
        }

        packetHandler.addPacketToQueue(new PacketValidateLayoutsResponse(changedLayouts));
        //System.out.println("LoggedInListener.onValidateResponse { \"Validated layouts for client\" }");
    }
}
