package com.jabyftw.gameclient.maps.util;

import com.jabyftw.gameclient.screen.Animation;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.AnimationEnum;

/**
 * Created by Rafael on 29/12/2014.
 */
public enum Material {

    BLOCK_WALL(AnimationEnum.BLOCK_MATERIALS, 0),
    AIR(AnimationEnum.BLOCK_MATERIALS, 1),
    WATER(AnimationEnum.WATER_MATERIAL),
    CLOSED_DOOR(AnimationEnum.DOOR_MATERIAL, 0),
    OPEN_DOOR(AnimationEnum.DOOR_MATERIAL, 1);

    private final AnimationEnum animationEnum;
    private final int spriteSheetFrame;

    /**
     * Static-image material
     *
     * @param animationEnum    the animation enum for resource use
     * @param spriteSheetFrame the frame on the sprite sheet
     */
    private Material(AnimationEnum animationEnum, int spriteSheetFrame) {
        if(spriteSheetFrame < 0) throw new IllegalArgumentException("For animated material, use other constructor");
        this.animationEnum = animationEnum;
        this.spriteSheetFrame = spriteSheetFrame;
    }

    /**
     * Animated material
     *
     * @param animationEnum the animation enum for resource use
     */
    private Material(AnimationEnum animationEnum) {
        this.animationEnum = animationEnum;
        this.spriteSheetFrame = -1;
    }

    public Animation getAnimation() {
        Animation animation = Resources.getAnimation(animationEnum);
        return spriteSheetFrame >= 0 ? animation.setCurrentFrame(spriteSheetFrame) : animation;
    }

    public boolean isTransparent() {
        switch(this) {
            case AIR:
            case OPEN_DOOR:
            case WATER:
                return true;
            default:
                return false;
        }
    }

    public boolean isSolid() {
        switch(this) {
            case BLOCK_WALL:
            case CLOSED_DOOR:
                return true;
            default:
                return false;
        }
    }

    public float getSpeedMultiplier() {
        switch(this) {
            case WATER:
                return 0.65f;
            default:
                return 1;
        }
    }

    public boolean appearOnMapEditor() {
        return this != OPEN_DOOR;
    }

    public static Material getDefaultMaterial() {
        return BLOCK_WALL;
    }

    public static Material valueOf(int ordinal) {
        if(ordinal >= Material.values().length)
            ordinal = 0;
        else if(ordinal < 0)
            ordinal = Material.values().length - 1;

        for(Material material : Material.values()) {
            if(material.ordinal() == ordinal) {
                return material;
            }
        }
        return null;
    }
}
