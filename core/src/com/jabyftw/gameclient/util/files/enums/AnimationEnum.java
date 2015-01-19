package com.jabyftw.gameclient.util.files.enums;

/**
 * Created by Rafael on 15/12/2014.
 */
public enum AnimationEnum {

    PLAYER_ANIMATION(TextureEnum.PLAYER_TEXTURE, -1, false, 8, 8, 1),
    TARGET_ANIMATION(TextureEnum.TARGET_TEXTURE, -1, false, 8, 8, 1),
    WATER_MATERIAL(TextureEnum.WATER_TEXTURE, 1/3f, true, 8, 8, 1),
    BLOCK_MATERIALS(TextureEnum.MATERIALS_TEXTURE, -1, false, 8, 8, 1),
    DOOR_MATERIAL(TextureEnum.DOOR_TEXTURE, -1, false, 8, 8, 1);

    private final TextureEnum textureEnum;
    private final float frameDelay;
    private final boolean loop;
    private final int tileWidth, tileHeight, spriteSheetLine;

    private AnimationEnum(TextureEnum textureEnum, float frameDelay, boolean loop, int tileWidth, int tileHeight, int spriteSheetLine) {
        this.textureEnum = textureEnum;
        this.frameDelay = frameDelay;
        this.loop = loop;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.spriteSheetLine = --spriteSheetLine;
    }

    public TextureEnum getTextureEnum() {
        return textureEnum;
    }

    public float getFrameDelay() {
        return frameDelay;
    }

    public boolean isLooping() {
        return loop;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getSpriteSheetLine() {
        return spriteSheetLine;
    }
}
