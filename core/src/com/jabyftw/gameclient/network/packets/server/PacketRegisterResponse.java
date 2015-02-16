package com.jabyftw.gameclient.network.packets.server;

import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.LoginResponse;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;

/**
 * Created by Rafael on 13/01/2015.
 */
public class PacketRegisterResponse extends PacketLoginResponse {

    public PacketRegisterResponse(LoginResponse loginResponse) {
        this();
        this.loginResponse = loginResponse;
    }

    public PacketRegisterResponse(OnlinePlayerProfile playerProfile) {
        this();
        this.loginResponse = LoginResponse.SUCCESSFUL_LOGIN;
        this.playerProfile = playerProfile;
    }

    public PacketRegisterResponse() {
        super(PacketType.REGISTER_RESPONSE);
    }
}
