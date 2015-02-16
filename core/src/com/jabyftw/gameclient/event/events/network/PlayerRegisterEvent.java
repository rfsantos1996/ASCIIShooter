package com.jabyftw.gameclient.event.events.network;

import com.jabyftw.gameclient.event.EventType;
import com.jabyftw.gameclient.network.packets.server.PacketRegisterResponse;

/**
 * Created by Rafael on 09/02/2015.
 */
public class PlayerRegisterEvent extends PlayerLoginEvent {

    public PlayerRegisterEvent(PacketRegisterResponse registerResponse) {
        super(EventType.PLAYER_REGISTER_EVENT, registerResponse);
    }

    @Override
    public PacketRegisterResponse getLoginResponse() {
        return (PacketRegisterResponse) super.getLoginResponse();
    }
}
