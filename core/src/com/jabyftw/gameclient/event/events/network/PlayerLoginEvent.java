package com.jabyftw.gameclient.event.events.network;

import com.jabyftw.gameclient.event.EventType;
import com.jabyftw.gameclient.event.util.AbstractEvent;
import com.jabyftw.gameclient.network.packets.server.PacketLoginResponse;

/**
 * Created by Rafael on 09/02/2015.
 */
public class PlayerLoginEvent extends AbstractEvent {

    private final PacketLoginResponse loginResponse;

    public PlayerLoginEvent(PacketLoginResponse loginResponse) {
        this(EventType.PLAYER_LOGIN_EVENT, loginResponse);
    }

    PlayerLoginEvent(EventType eventType, PacketLoginResponse loginResponse) {
        super(eventType);
        this.loginResponse = loginResponse;
    }

    public PacketLoginResponse getLoginResponse() {
        return loginResponse;
    }
}
