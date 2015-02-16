package com.jabyftw.gameclient.screen;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Rafael on 14/02/2015.
 */
public class PlayerLight extends PointLight {

    public PlayerLight(RayHandler rayHandler, int rays) {
        super(rayHandler, rays);
    }

    public PlayerLight(RayHandler rayHandler, int rays, Color color, float distance, float x, float y) {
        super(rayHandler, rays, color, distance, x, y);
    }

    @Override
    public void setDistance(float dist) {
        super.setDistance(dist);
        this.distance = dist; // Remove 0.1f cap.
    }
}
