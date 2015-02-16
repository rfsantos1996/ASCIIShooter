package com.jabyftw.gameclient.util.tools;

import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Rafael on 05/02/2015.
 */
public class LogMessage implements Tickable {

    private final String message;
    private final float duration;
    private float timeAlive;

    public LogMessage(String message, float duration) {
        this.message = message;
        this.duration = duration;
    }

    @Override
    public void update(float deltaTime) {
        this.timeAlive += deltaTime;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAlive() {
        return timeAlive <= duration || duration <= 0;
    }
}
