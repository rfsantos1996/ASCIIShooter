package com.jabyftw.gameclient.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jabyftw.gameclient.util.Util;

/**
 * Created by Rafael on 08/12/2014.
 */
public class MovableCamera extends OrthographicCamera {

    private float minX, maxX, minY, maxY;

    public MovableCamera() {
        this(-1, -1, -1, -1);
    }

    public MovableCamera(float minX, float maxX, float minY, float maxY) {
        super();
        setCameraBounds(minX, maxX, minY, maxY);
    }

    public void setCameraBounds(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public void setCameraBounds(Vector2 min, Vector2 max) {
        this.minX = min.x;
        this.minY = min.y;
        this.maxX = max.x;
        this.maxY = max.y;
    }

    public void updatePosition(Vector2 vector2, boolean smooth) {
        if(smooth) {
            // Location smoothness
            Vector3 targetPosition = new Vector3(vector2, 0);
            float scale = targetPosition.dst2(position) / (Util.square(3 * viewportWidth / 5f) + Util.square(3 * viewportHeight / 5f));
            position.add(targetPosition.cpy().sub(position.cpy()).scl(scale > 1 ? 1 : scale));
        } else {
            position.set(new Vector3(vector2, 0));
        }
        fixBounds();
    }

    private void fixBounds() {
        if(minX < 0 && maxX < 0 && minY < 0 && maxY < 0)
            return;

        float halfWidth = viewportWidth / 2f;
        float halfHeight = viewportHeight / 2f;

        if(position.x < minX + halfWidth) position.x = minX + halfWidth;
        if(position.x > maxX - halfWidth) position.x = maxX - halfWidth;
        if(position.y < minY + halfHeight) position.y = minY + halfHeight;
        if(position.y > maxY - halfHeight) position.y = maxY - halfHeight;
    }

    public Vector2[] getBounds() {
        return new Vector2[]{
                new Vector2(minX, minY),
                new Vector2(maxX, maxY)
        };
    }
}
