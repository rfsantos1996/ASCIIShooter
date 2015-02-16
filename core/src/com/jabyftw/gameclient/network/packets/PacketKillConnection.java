package com.jabyftw.gameclient.network.packets;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.network.util.Packet;
import com.jabyftw.gameclient.network.PacketType;
import com.jabyftw.gameclient.network.util.PacketHandler;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 12/01/2015.
 */
public class PacketKillConnection extends Packet {

    private Reason reason;

    public PacketKillConnection(Reason reason) {
        super(PacketType.KILL_CONNECTION);
        this.reason = reason;
    }

    public PacketKillConnection() {
        super(PacketType.KILL_CONNECTION);
    }

    public Reason getReason() {
        return reason;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.reason = Reason.valueOf(jsonData.getInt("reason"));
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("reason", reason.ordinal(), Integer.class);
    }

    public enum Reason {

        /*
         * From client
         */
        CLIENT_CLOSING_CONNECTION(),
        CLIENT_LOST_CONNECTION(),
        CLIENT_BAD_PACKET_RECEIVED(),
        /*
         * From server
         */
        SERVER_UNKNOWN(LangEnum.UNKNOWN_REASON),
        SERVER_SHUTTING_DOWN(LangEnum.SERVER_DISCONNECTING_REASON),
        SERVER_YOU_TIMED_OUT(LangEnum.TIMED_OUT_REASON),
        SERVER_BAD_PACKET_RECEIVED(LangEnum.BAD_PACKET_REASON),
        SERVER_MISMATCHED_VERSION(LangEnum.MISMATCHED_VERSIONS_REASON);

        private final PacketHandler.Type reasonSender;
        private final LangEnum disconnectMessage;

        private Reason() {
            this.reasonSender = PacketHandler.Type.CLIENT;
            this.disconnectMessage = null;
        }

        private Reason(LangEnum disconnectMessage) {
            this.reasonSender = PacketHandler.Type.SERVER;
            this.disconnectMessage = disconnectMessage;
        }

        public PacketHandler.Type getReasonSender() {
            return reasonSender;
        }

        public String getDisconnectMessage() {
            return disconnectMessage == null ? "You shouldn't received this thing, really O.o" : Resources.getLang(disconnectMessage);
        }

        public static Reason valueOf(int ordinal) {
            return values()[ordinal];
        }
    }
}
