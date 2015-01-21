package com.jabyftw.gameclient.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jabyftw.gameclient.util.Util;

/**
 * Created by Rafael on 08/12/2014.
 */
public class MovableCamera extends OrthographicCamera {

    private final Vector2 min = new Vector2(-1, -1),
            max = new Vector2(-1, -1);

    public MovableCamera() {
    }

    public MovableCamera(float minX, float minY, float maxX, float maxY) {
        super();
        setCameraBounds(minX, minY, maxX, maxY);
    }

    public void setCameraBounds(float minX, float minY, float maxX, float maxY) {
        this.min.set(minX, minY);
        this.max.set(maxX, maxY);
    }

    public void setCameraBounds(Vector2 minScreenCoordinates, Vector2 maxScreenCoordinates) {
        System.out.println("CameraBounds -> { min: " + minScreenCoordinates.toString() + " max: " + maxScreenCoordinates.toString() + " }");
        this.min.set(minScreenCoordinates);
        this.max.set(maxScreenCoordinates);
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
        if(min.x < 0 && max.x < 0 && min.y < 0 && max.y < 0)
            return;

        float halfWidth = viewportWidth / 2f;
        float halfHeight = viewportHeight / 2f;

        if(position.x < min.x + halfWidth) position.x = min.x + halfWidth;
        if(position.x > max.x - halfWidth) position.x = max.x - halfWidth;
        if(position.y < min.y + halfHeight) position.y = min.y + halfHeight;
        if(position.y > max.y - halfHeight) position.y = max.y - halfHeight;
    }

    public Vector2[] getBounds() {
        return new Vector2[]{min, max};
    }
}
