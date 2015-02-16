package com.jabyftw.gameclient.entity.weapon.util;

import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.entity.EntityManager;
import com.jabyftw.gameclient.entity.weapon.weapons.AssaultRiflesProperties;
import com.jabyftw.gameclient.entity.weapon.weapons.MachinePistolsProperties;
import com.jabyftw.gameclient.entity.weapon.weapons.SubmachineGunsProperties;
import com.jabyftw.gameclient.entity.weapon.weapons.WeaponType;
import com.jabyftw.gameclient.util.Constants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by Rafael on 07/02/2015.
 */
public abstract class WeaponProperties {

    private static final float MAXIMUM_LIGHT_DISTANCE = 1f;
    private static final float MINIMUM_LIGHT_DISTANCE = 0.015f;
    private static final float DIFFERENCE_FROM_HIGHEST_RPM_TO_LOWEST = 700f;

    private static final HashMap<WeaponType, Array<WeaponProperties>> weaponsPropertiesMap = new HashMap<WeaponType, Array<WeaponProperties>>();
    private static final Array<WeaponProperties> emptyArray = new Array<WeaponProperties>();

    private final WeaponType weaponType;
    private final Class<? extends WeaponController> clazz;
    private final String weaponName;
    private final int roundsPerMinute, rounds, bulletSpeed;
    private final float reloadTime, bulletsToKillPlayer;

    private int unlockLevel;

    protected WeaponProperties(Class<? extends WeaponController> clazz, WeaponType weaponType, String weaponName, int roundsPerMinute, float reloadTime, int rounds, int bulledSpeed, float bulletsToKillPlayer) {
        this.clazz = clazz;
        this.weaponType = weaponType;
        this.weaponName = weaponName;
        this.roundsPerMinute = roundsPerMinute;
        this.reloadTime = reloadTime;
        this.rounds = rounds;
        this.bulletSpeed = bulledSpeed;
        this.bulletsToKillPlayer = bulletsToKillPlayer;
        {
            if(!weaponsPropertiesMap.containsKey(weaponType))
                weaponsPropertiesMap.put(weaponType, new Array<WeaponProperties>());
            weaponsPropertiesMap.get(weaponType).add(this);
        }
    }

    public String getWeaponName() {
        return weaponName;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    void setUnlockLevel(int unlockLevel) {
        this.unlockLevel = unlockLevel;
    }

    public int getLevelRequired() {
        return unlockLevel;
    }

    public float getFiringDelay() {
        return 60f / roundsPerMinute;
    }

    public float getReloadDelay() {
        return reloadTime;
    }

    public int getMaximumWeaponCapacity() {
        return rounds;
    }

    public float getBulletSpeedMetersSecond() {
        return bulletSpeed;
    }

    public float getLightDistance() {
        return ((MAXIMUM_LIGHT_DISTANCE - MINIMUM_LIGHT_DISTANCE) / DIFFERENCE_FROM_HIGHEST_RPM_TO_LOWEST) * bulletSpeed;
    }

    public float getBulletDamage() {
        return Constants.Gameplay.Entities.DEFAULT_HEALTH / bulletsToKillPlayer;
    }

    public int ordinal() {
        return weaponsPropertiesMap.get(getWeaponType()).indexOf(this, true);
    }

    public WeaponController toWeaponController(EntityManager entityManager) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor(EntityManager.class, WeaponProperties.class);
            return (WeaponController) constructor.newInstance(entityManager, this);
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(InstantiationException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initializeWeapons() {
        AssaultRiflesProperties.initialize();
        MachinePistolsProperties.initialize();
        SubmachineGunsProperties.initialize();

        Constants.Gameplay.Player.MAXIMUM_PROFILE_LEVEL = calculateUnlockLevels();
    }

    private static int calculateUnlockLevels() {
        HashMap<WeaponType, Float> weaponsUntilInsert = new HashMap<WeaponType, Float>(),
                actualWeaponsUntilInsert = new HashMap<WeaponType, Float>();

        Array<WeaponProperties> weaponProperties = new Array<WeaponProperties>(values());
        WeaponType[] weaponTypes = WeaponType.values();

        {
            for(WeaponType weaponType : weaponTypes) {
                WeaponProperties[] values = values(weaponType);

                weaponsUntilInsert.put(weaponType, (float) weaponProperties.size / (float) values.length);
            }
            actualWeaponsUntilInsert.putAll(weaponsUntilInsert);
        }

        NavigableMap<Float, WeaponType> weaponTypeMap = new TreeMap<Float, WeaponType>();
        {
            float total = 0;

            for(WeaponProperties ignored : weaponProperties) {
                WeaponType lowestWeaponType = null;
                {
                    float lowest = Float.MAX_VALUE;
                    for(Map.Entry<WeaponType, Float> entry : actualWeaponsUntilInsert.entrySet()) {
                        entry.setValue(entry.getValue() - 1);

                        if(lowest > entry.getValue()) {
                            lowestWeaponType = entry.getKey();
                            lowest = entry.getValue();
                        }
                    }
                    actualWeaponsUntilInsert.put(lowestWeaponType, weaponsUntilInsert.get(lowestWeaponType));
                }
                weaponTypeMap.put(total = (total + weaponsUntilInsert.get(lowestWeaponType)), lowestWeaponType);
            }
        }

        int latestLevel = 0;
        {
            HashMap<WeaponType, Integer> latestOrdinal = new HashMap<WeaponType, Integer>();

            while(!weaponTypeMap.isEmpty()) {
                Map.Entry<Float, WeaponType> entry = weaponTypeMap.pollFirstEntry();

                latestLevel += 1;
                latestOrdinal.put(entry.getValue(), latestOrdinal.getOrDefault(entry.getValue(), -1) + 1);
                int ordinal = latestOrdinal.get(entry.getValue());

                WeaponProperties weaponProperty = WeaponProperties.values(entry.getValue())[ordinal];
                weaponProperty.setUnlockLevel(latestLevel);
                //System.out.println("unlockLevel: " + latestLevel + " type: " + weaponProperty.getWeaponType().name() + " name: " + weaponProperty.getWeaponName());
            }
        }

        System.out.println("WeaponProperties.calculateUnlockLevels { latestLevel = " + latestLevel + " weaponsSize = " + weaponProperties.size + " }");
        return latestLevel;
    }

    public static WeaponProperties valueOf(WeaponType weaponType, int ordinal) {
        return weaponsPropertiesMap.get(weaponType).get(ordinal);
    }

    public static Array<WeaponProperties> values(short weaponSlotType) {
        Array<WeaponProperties> weaponPropertiesArray = new Array<WeaponProperties>();

        for(WeaponType weaponType : WeaponType.values()) {
            for(short weaponTypeShort : weaponType.getPossibleTypes()) {
                if(weaponTypeShort == weaponSlotType)
                    values(weaponPropertiesArray, weaponType);
            }
        }

        return weaponPropertiesArray;
    }

    private static WeaponProperties[] values(WeaponType weaponType) {
        return values(new Array<WeaponProperties>(), weaponType).toArray(WeaponProperties.class);
    }

    private static WeaponProperties[] values() {
        Array<WeaponProperties> weaponPropertiesArray = new Array<WeaponProperties>();
        for(WeaponType type : WeaponType.values()) {
            values(weaponPropertiesArray, type);
        }
        return weaponPropertiesArray.toArray(WeaponProperties.class);
    }

    private static Array<WeaponProperties> values(Array<WeaponProperties> weaponPropertiesArray, WeaponType weaponType) {
        weaponPropertiesArray.addAll(weaponsPropertiesMap.getOrDefault(weaponType, emptyArray));
        return weaponPropertiesArray;
    }
}
