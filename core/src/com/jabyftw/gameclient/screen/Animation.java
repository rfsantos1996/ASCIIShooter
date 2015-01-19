package com.jabyftw.gameclient.screen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Rafael on 13/12/2014.
 */
public class Animation implements Tickable {

    private final TextureRegion[] frames;
    private final float frameDelay;
    private final boolean loop;

    private float elapsedTime;
    private int currentFrame, timesPlayed;

    public Animation(Animation animation) {
        this(animation.frameDelay, animation.loop, animation.frames);
    }

    public Animation(float frameDelay, boolean loop, TextureRegion[] frames) {
        this.frames = frames;
        this.loop = loop;
        this.frameDelay = frameDelay;
        clearAnimation();
    }

    @Override
    public void update(float deltaTime) {
        if(frameDelay <= 0) return;
        elapsedTime += deltaTime;

        while(elapsedTime >= frameDelay) {

            elapsedTime -= frameDelay;
            currentFrame++;

            if(currentFrame >= frames.length) {
                if(loop) {
                    currentFrame = 0;
                    timesPlayed++;
                } else {
                    currentFrame = frames.length - 1;
                }
            }
        }
    }

    public Animation setCurrentFrame(int currentFrame) {
        currentFrame = fixFrameForExceptions(currentFrame);
        this.currentFrame = currentFrame;

        return this;
    }

    public TextureRegion getCurrentFrame() {
        return frames[currentFrame];
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void clearAnimation() {
        elapsedTime = 0;
        currentFrame = 0;
        timesPlayed = 0;
    }

    public TextureRegion getFrame(int frame) {
        frame = fixFrameForExceptions(frame);
        return frames[frame];
    }

    private int fixFrameForExceptions(int frame) {
        if(frame >= frames.length)
            frame = frames.length - 1;
        else if(frame < 0)
            frame = 0;
        return frame;
    }
}
