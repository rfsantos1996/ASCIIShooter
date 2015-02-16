package com.jabyftw.gameclient.network.listeners;

import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.server.PacketValidateLayoutsResponse;

/**
 * Created by Rafael on 11/02/2015.
 */
public class LoggedInListener extends AbstractPacketListener {

    public LoggedInListener() {
        super(Main.getPacketHandler(), true);
        validPacketTypes.add(PacketType.VALIDATE_LAYOUT_RESPONSE);
    }

    @PacketListener
    public void onValidateResponse(PacketValidateLayoutsResponse validateResponse) {
        Layout[] profileLayouts = Main.getOnlineProfile().getLayouts();

        for(Layout layout : profileLayouts) {
            Main.getOnlineProfile().getLayouts()[layout.getIndex()] = layout;
        }

        //System.arraycopy(validateResponse.getLayouts(), 0, profileLayouts, 0, profileLayouts.length);
        //System.out.println("LoggedInListener.onValidateResponse { \"Validated layouts.\" }");
    }
}
