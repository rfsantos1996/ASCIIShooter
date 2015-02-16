package com.jabyftw.gameclient.event;

/**
 * Created by Rafael on 06/02/2015.
 */
public enum EventType {

    /*
     * NETWORK
     */
    PLAYER_LOGIN_EVENT(false),
    PLAYER_REGISTER_EVENT(false),
    /*
     * PACKET HANDLER
     */
    PRE_CLOSE_CONNECTION_EVENT(false),
    POST_CLOSE_CONNECTION_EVENT(false),
    ADD_PACKET_TO_QUEUE_EVENT(false),
    SEND_PACKET_EVENT(false),
    RECEIVE_PACKET_EVENT(false),
    SEND_PACKET_ERROR_EVENT(false),
    RECEIVE_PACKET_ERROR_EVENT(false);

    private final boolean runOnMainThread;

    /*private EventType() {
        this(true);
    }*/

    private EventType(boolean runOnMainThread) {
        this.runOnMainThread = runOnMainThread;
    }

    public boolean runOnMainThread() {
        return runOnMainThread;
    }
}
