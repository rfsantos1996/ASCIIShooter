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
            /*Vector2 returning;
            System.out.println(name() + " to " + "screen" + "  @ " + vector2.toString() + " -> " + (returning = vector2.scl(Main.PIXELS_PER_METER)).toString());
            return returning;*/
            return vector2.scl(Main.PIXELS_PER_METER);
        }

        @Override
        public Vector2 toWorldCoordinates(Vector2 vector2) {
            /*Vector2 returning;
            System.out.println(name() + " to " + "world" + "  @ " + vector2.toString() + " -> " + (returning = vector2.scl(1 / BOX2D_TILE_SCALE_WIDTH, 1 / BOX2D_TILE_SCALE_HEIGHT)).toString());
            return returning;*/
            return vector2.scl(1 / BOX2D_TILE_SCALE_WIDTH, 1 / BOX2D_TILE_SCALE_HEIGHT);
        }
    },

    WORLD_COORDINATES {
        @Override
        public Vector2 toBox2dCoordinates(Vector2 vector2) {
            /*Vector2 returning;
            System.out.println(name() + " to " + "box2d" + "  @ " + vector2.toString() + " -> " + (returning = vector2.scl(BOX2D_TILE_SCALE_WIDTH, BOX2D_TILE_SCALE_HEIGHT)).toString());
            return returning;*/
            return vector2.scl(BOX2D_TILE_SCALE_WIDTH, BOX2D_TILE_SCALE_HEIGHT);
        }

        @Override
        public Vector2 toScreenCoordinates(Vector2 vector2) {
            /*Vector2 returning;
            System.out.println(name() + " to " + "screen" + "  @ " + vector2.toString() + " -> " + (returning = vector2.scl(TILE_SCALE_WIDTH, TILE_SCALE_HEIGHT)).toString());
            return returning;*/
            return vector2.scl(TILE_SCALE_WIDTH, TILE_SCALE_HEIGHT);
        }
    },

    SCREEN_COORDINATES {
        @Override
        public Vector2 toBox2dCoordinates(Vector2 vector2) {
            /*Vector2 returning;
            System.out.println(name() + " to " + "box2d" + "  @ " + vector2.toString() + " -> " + (returning = vector2.scl(1 / Main.PIXELS_PER_METER)).toString());
            return returning;*/
            return vector2.scl(1 / Main.PIXELS_PER_METER);
        }

        @Override
        public Vector2 toWorldCoordinates(Vector2 vector2) {
            Vector2 returning;
            System.out.println(name() + " to " + "world" + "  @ " + vector2.toString() + " -> " + (returning = vector2.scl(1 / TILE_SCALE_WIDTH, 1 / TILE_SCALE_HEIGHT)).toString());
            return returning;
            //return vector2.scl(1 / TILE_SCALE_WIDTH, 1 / TILE_SCALE_HEIGHT);
        }
    };

    public static final float TILE_SCALE_WIDTH = Map.BASE_TILE_SCALE * Map.TILE_WIDTH;
    public static final float TILE_SCALE_HEIGHT = Map.BASE_TILE_SCALE * Map.TILE_HEIGHT;

    public static final float BOX2D_TILE_SCALE_WIDTH = TILE_SCALE_WIDTH / Main.PIXELS_PER_METER;
    public static final float BOX2D_TILE_SCALE_HEIGHT = TILE_SCALE_HEIGHT / Main.PIXELS_PER_METER;

    public Vector2 toBox2dCoordinates(Vector2 vector2) {
        throw new AbstractMethodError("This method was not implemented yet");
    }

    public Vector2 toWorldCoordinates(Vector2 vector2) {
        throw new AbstractMethodError("This method was not implemented yet");
    }

    public Vector2 toScreenCoordinates(Vector2 vector2) {
        throw new AbstractMethodError("This method was not implemented yet");
    }
}
