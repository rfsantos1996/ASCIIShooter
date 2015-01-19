package com.jabyftw.gameserver.network;

import com.badlogic.gdx.net.Socket;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.network.util.PacketHandler;

/**
 * Created by Rafael on 13/01/2015.
 */
public class ServerPacketHandler extends PacketHandler {

    public ServerPacketHandler(Socket connection) {
        super(connection);
    }

    @Override
    public void receivePacket(Packet packet) {
    }
}
