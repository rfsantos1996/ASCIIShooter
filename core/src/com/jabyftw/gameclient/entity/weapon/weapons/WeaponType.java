package com.jabyftw.gameclient.entity.weapon.weapons;

import com.badlogic.gdx.math.Vector2;
import com.jabyftw.gameclient.entity.weapon.WeaponSlotType;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;
import com.jabyftw.gameclient.maps.Converter;

/**
 * Created by Rafael on 07/02/2015.
 */
public enum WeaponType {

    /*
     * THIS NEEDS TO BE ORDERED (OLDEST ON TOP)!!!
     */
    ASSAULT_RIFLE(AssaultRiflesProperties.class, 20f, 14f, new short[]{WeaponSlotType.PRIMARY.getType()}),
    MACHINE_PISTOLS(MachinePistolsProperties.class, 11f, 7.5f, new short[]{WeaponSlotType.SECONDARY.getType()}),
    SUBMACHINE_GUNS(SubmachineGunsProperties.class, 15f, 9.5f, new short[]{WeaponSlotType.PRIMARY.getType()});

    private final Class<? extends WeaponProperties> clazz;
    private final float maxDistance, effectiveDistance;
    private final short[] type;

    private WeaponType(Class<? extends WeaponProperties> controller, float maxDistance, float effectiveDistance, short[] type) {
        Vector2 box2dCoordinates = Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(maxDistance, effectiveDistance)); // x = maxDistance, y = effectiveDistance
        this.clazz = controller;
        this.maxDistance = box2dCoordinates.x;
        this.effectiveDistance = box2dCoordinates.y; // Distance to drop the damage to 50%
        this.type = type;
    }

    public Class<? extends WeaponProperties> getController() {
        return clazz;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public float getEffectiveDistance() {
        return effectiveDistance;
    }

    public short[] getPossibleTypes() {
        return type;
    }

    public static WeaponType valueOf(int ordinal) {
        return values()[ordinal];
    }
}
