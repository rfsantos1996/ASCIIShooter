package com.jabyftw.gameserver.gamestate.controller;

import com.badlogic.gdx.utils.Base64Coder;
import com.jabyftw.gameclient.event.Listener;
import com.jabyftw.gameclient.event.events.network.PlayerLoginEvent;
import com.jabyftw.gameclient.event.events.network.PlayerRegisterEvent;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.packets.PacketKillConnection;
import com.jabyftw.gameclient.network.packets.client.PacketLoginRequest;
import com.jabyftw.gameclient.network.packets.client.PacketRegisterRequest;
import com.jabyftw.gameclient.network.packets.server.PacketLoginResponse;
import com.jabyftw.gameclient.network.packets.server.PacketRegisterResponse;
import com.jabyftw.gameclient.network.util.LoginResponse;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;
import com.jabyftw.gameserver.Server;
import com.jabyftw.gameserver.network.ServerPacketHandler;

/**
 * Created by Rafael on 06/02/2015.
 */
public class BeforeLoginListener extends AbstractPacketListener {

    public BeforeLoginListener(ServerPacketHandler packetHandler) {
        super(packetHandler);
        validPacketTypes.add(PacketType.LOGIN_REQUEST);
        validPacketTypes.add(PacketType.REGISTER_REQUEST);
    }

    @Listener.PacketListener(deliverType = PacketListener.DeliverType.RECEIVED)
    public void onLoginRequest(PacketLoginRequest loginRequest) {
        if(Constants.GAME_VERSION != loginRequest.getGameVersion()) {
            packetHandler.closeThread(PacketKillConnection.Reason.SERVER_MISMATCHED_VERSION);
            return;
        }

        if(packetHandler.getLoginResponse() == LoginResponse.SUCCESSFUL_LOGIN) {
            // Do not let being spammed even if already logged in!
            packetHandler.addPacketToQueue(loginRequest instanceof PacketRegisterRequest ?
                            new PacketRegisterResponse(LoginResponse.SUCCESSFUL_LOGIN) :
                            new PacketLoginResponse(LoginResponse.SUCCESSFUL_LOGIN)
            );
            return;
        }

        if(Server.getInstance().connectionThreadExists(loginRequest.getUsername())) {
            packetHandler.addPacketToQueue(loginRequest instanceof PacketRegisterRequest ?
                    new PacketRegisterResponse(LoginResponse.USERNAME_ALREADY_LOGGED_IN) :
                    new PacketLoginResponse(LoginResponse.USERNAME_ALREADY_LOGGED_IN));
            return;
        }

        PacketLoginResponse loginResponse = (loginRequest instanceof PacketRegisterRequest ?
                handleRegister((PacketRegisterRequest) loginRequest) :
                handleLogin(loginRequest)
        );

        PlayerLoginEvent loginEvent;

        if(loginResponse instanceof PacketRegisterResponse)
            loginEvent = new PlayerRegisterEvent((PacketRegisterResponse) loginResponse);
        else
            loginEvent = new PlayerLoginEvent(loginResponse);

        Server.getEventHandler().callEvent(loginEvent);
        packetHandler.setLoginResponse(loginResponse.getLoginResponse());

        if(packetHandler.getLoginResponse() == LoginResponse.SUCCESSFUL_LOGIN) {
            packetHandler.setOnlinePlayerProfile(loginResponse.getPlayerProfile());
            packetHandler.setConnectionName(loginResponse.getPlayerProfile().getPlayerName());
            packetHandler.removeListener(this);
            packetHandler.addListener(new LoggedInListener(packetHandler, loginResponse instanceof PacketRegisterResponse));
        }

        packetHandler.addPacketToQueue(loginResponse);
    }

    /*
     * HANDLE LOGIN
     */
    private PacketLoginResponse handleLogin(PacketLoginRequest loginRequest) {
        String decodedPassword;
        { // Check if client changed the encryption
            try {
                decodedPassword = Base64Coder.decodeString(loginRequest.getPassword());
            } catch(IllegalArgumentException e) {
                return new PacketRegisterResponse(LoginResponse.UNKNOWN_ERROR);
            }
        }

        { // Check text lengths
            LoginResponse loginResponse;
            if((loginResponse = checkTextsLength(loginRequest, decodedPassword)) != null) {
                return new PacketLoginResponse(loginResponse);
            }
        }

        OnlinePlayerProfile playerProfile = new OnlinePlayerProfile(); // TODO: check login on MySQL
        playerProfile.setPlayerName(loginRequest.getUsername());

        return new PacketLoginResponse(playerProfile);
    }

    /*
     * HANDLE REGISTER
     */
    private PacketRegisterResponse handleRegister(PacketRegisterRequest registerRequest) {
        String decodedPassword;
        { // Check if client changed the encryption
            try {
                decodedPassword = Base64Coder.decodeString(registerRequest.getPassword());
                Base64Coder.decodeString(registerRequest.getSecondPassword());
            } catch(IllegalArgumentException e) {
                return new PacketRegisterResponse(LoginResponse.UNKNOWN_ERROR);
            }
        }

        // Checking if passwords match
        if(!registerRequest.getPassword().equals(registerRequest.getSecondPassword())) {
            //System.out.println("Passwords mismatching: "+ registerRequest.getPassword() + " "+ registerRequest.getSecondPassword());
            return new PacketRegisterResponse(LoginResponse.PASSWORD_MISMATCHING);
        }

        { // Check password length
            LoginResponse loginResponse;
            if((loginResponse = checkTextsLength(registerRequest, decodedPassword)) != null)
                return new PacketRegisterResponse(loginResponse);
        }

        OnlinePlayerProfile playerProfile = new OnlinePlayerProfile(); // TODO: register on MySQL
        playerProfile.setPlayerName(registerRequest.getUsername());

        return new PacketRegisterResponse(playerProfile);
    }

    public LoginResponse checkTextsLength(PacketLoginRequest loginRequest, String decodedString) {
        if(decodedString.length() <= Constants.Gameplay.TEXT_TOO_SHORT)
            return LoginResponse.PASSWORD_TOO_SHORT;
        if(decodedString.length() > Constants.Gameplay.TEXT_TOO_LONG)
            return LoginResponse.PASSWORD_TOO_LONG;

        if(loginRequest.getUsername().length() <= Constants.Gameplay.TEXT_TOO_SHORT)
            return LoginResponse.USERNAME_TOO_SHORT;
        if(loginRequest.getUsername().length() > Constants.Gameplay.TEXT_TOO_LONG)
            return LoginResponse.USERNAME_TOO_LONG;

        return null;
    }
}
