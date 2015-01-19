package com.jabyftw.gameclient.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.network.util.PacketHandler;

/**
 * Created by Rafael on 13/01/2015.
 */
public class ClientPacketHandler extends PacketHandler {

    public ClientPacketHandler() throws GdxRuntimeException {
        super(Gdx.net.newClientSocket(Net.Protocol.TCP, "localhost", PacketHandler.CONNECTION_PORT, null));
    }

    @Override
    public void receivePacket(Packet packet) {
    }
}
