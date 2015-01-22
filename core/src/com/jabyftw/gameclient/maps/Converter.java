package com.jabyftw.gameclient.maps;

import com.badlogic.gdx.math.Vector2;
import com.jabyftw.gameclient.Main;

/**
 * Created by Rafael on 21/01/2015
 * <p/>
 * WORLD:  1, 2, 3
 * SCREEN: 16, 24, 32
 * BOX2D:  0.16, 0.24, 0.32
 * <p/>
 * Box2d -> Screen: * PP
 * Box2d -> World: / BTSW, / BTSH
 * <p/>
 * World -> Box2d: * BTSW, * BTSH
 * World -> Screen: * TSW, * TS
 * <p/>
 * Screen -> Box2d: / PPM
 * Screen -> World: / TSW, / TSW
 */
public enum Converter {

    BOX2D_COORDINATES {
        @Override
        public Vector2 toScreenCoordinates(Vector2 vector2) {
            return vector2.scl(Main.PIXELS_PER_METER);
        }

        @Override
        public Vector2 toWorldCoordinates(Vector2 vector2) {
            return vector2.scl(1 / BOX2D_TILE_SCALE.x, 1 / BOX2D_TILE_SCALE.y);
        }
    },

    WORLD_COORDINATES {
        @Override
        public Vector2 toBox2dCoordinates(Vector2 vector2) {
            return vector2.scl(BOX2D_TILE_SCALE.x, BOX2D_TILE_SCALE.y);
        }

        @Override
        public Vector2 toScreenCoordinates(Vector2 vector2) {
            return vector2.scl(TILE_SCALE);
        }
    },

    SCREEN_COORDINATES {
        @Override
        public Vector2 toBox2dCoordinates(Vector2 vector2) {
            return vector2.scl(1 / Main.PIXELS_PER_METER);
        }

        @Override
        public Vector2 toWorldCoordinates(Vector2 vector2) {
            return vector2.scl(1 / TILE_SCALE.x, 1 / TILE_SCALE.y);
        }
    };

    public static final Vector2 TILE_SCALE = new Vector2(Map.TILE_WIDTH, Map.TILE_HEIGHT).scl(Map.BASE_TILE_SCALE);
    public static final Vector2 BOX2D_TILE_SCALE = new Vector2(TILE_SCALE).scl(1 / Main.PIXELS_PER_METER);

    //public static final float TILE_SCALE_WIDTH = Map.BASE_TILE_SCALE * Map.TILE_WIDTH;
    //public static final float TILE_SCALE_HEIGHT = Map.BASE_TILE_SCALE * Map.TILE_HEIGHT;

    //public static final float BOX2D_TILE_SCALE_WIDTH = TILE_SCALE_WIDTH / Main.PIXELS_PER_METER;
    //public static final float BOX2D_TILE_SCALE_HEIGHT = TILE_SCALE_HEIGHT / Main.PIXELS_PER_METER;

    public Vector2 toBox2dCoordinates(Vector2 vector2) {
        throw new AbstractMethodError("You can't call this method on a Converter." + name() + "!");
    }

    public Vector2 toWorldCoordinates(Vector2 vector2) {
        throw new AbstractMethodError("You can't call this method on a Converter." + name() + "!");
    }

    public Vector2 toScreenCoordinates(Vector2 vector2) {
        throw new AbstractMethodError("You can't call this method on a Converter." + name() + "!");
    }
}
