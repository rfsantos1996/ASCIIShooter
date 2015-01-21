package com.jabyftw.gameclient.entity.util;

/**
 * Created by Rafael on 14/01/2015.
 */
public class Box2dConstants {

    /*
     * Category bits
     */
    public static final short BIT_EMPTY_BLOCK = 0;
    public static final short BIT_BLOCK = 2 << 1;
    public static final short BIT_PLAYER = 2 << 2;
    public static final short BIT_ENEMY = 2 << 3;
    public static final short BIT_BULLET = 2 << 4;
    public static final short BIT_LIGHT = 2 << 5;
    public static final short BIT_WORLD_BOUNDS = 2 << 6;
}
