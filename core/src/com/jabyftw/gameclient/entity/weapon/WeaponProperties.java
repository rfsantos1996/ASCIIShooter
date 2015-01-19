package com.jabyftw.gameclient.entity.weapon;

import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.entity.entities.EntityManager;
import com.jabyftw.gameclient.entity.entities.PlayerEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Isa on 04/01/2015.
 */
public enum WeaponProperties {

    // Machine pistols
    MAC_11("MAC-11", Type.MACHINE_PISTOLS, 1, 1200, 1.6f, 32, 980, 2.1f),
    GLOCK_18("Glock 18", Type.MACHINE_PISTOLS, 4, 1150, 2.1f, 33, 375, 1.9f),
    MINI_UZI("Mini-Uzi", Type.MACHINE_PISTOLS, 7, 950, 1.9f, 20, 375, 2.3f),

    // SMGs
    MP7("MP7", Type.SUBMACHINE_GUNS, 1, 950, 2.2f, 30, 735, 2.2f),
    MP9("MP9", Type.SUBMACHINE_GUNS, 3, 1000, 2.7f, 32, 390, 2.4f),
    UZI("Uzi", Type.SUBMACHINE_GUNS, 9, 600, 2.2f, 20, 400, 2.1f),
    P90("P90", Type.SUBMACHINE_GUNS, 12, 900, 3.0f, 50, 715, 2.5f),
    PP_90M1("PP-90M1", Type.SUBMACHINE_GUNS, 15, 800, 3.2f, 52, 320, 2.8f),

    // Assault rifles
    STG_44("StG 44", Type.ASSAULT_RIFLE, 1, 575, 3.1f, 30, 685, 3.7f),
    AUG("AUG", Type.ASSAULT_RIFLE, 2, 705, 2.6f, 32, 970, 3.1f),
    FAMAS_F1("Famas F1", Type.ASSAULT_RIFLE, 5, 950, 2.4f, 25, 960, 3.2f),
    FAMAS_G2("Famas G1", Type.ASSAULT_RIFLE, 6, 1050, 2.6f, 30, 925, 2.8f),
    L85A2("L85A2", Type.ASSAULT_RIFLE, 8, 695, 2.3f, 30, 940, 3.1f),
    GALIL_ACE("Galil ACE", Type.ASSAULT_RIFLE, 10, 700, 2.6f, 35, 710, 3.3f),
    SCAR_L("SCAR-L", Type.ASSAULT_RIFLE, 11, 625, 2.1f, 20, 875, 2.9f),
    AK_47("AK-47", Type.ASSAULT_RIFLE, 13, 600, 2.4f, 30, 715, 3.3f),
    M16("M16", Type.ASSAULT_RIFLE, 14, 815, 2.8f, 20, 985, 3.1f);

    public static final float BASE_BULLET_SPEED = 15f;

    private final Class aClass;

    private final String displayName;
    private final Type type;
    private final int levelRequired;
    // Weapon defaults
    private final float firingDelay, reloadDelay;
    private final int maximumWeaponCapacity;
    // Bullet defaults
    private final float bulletSpeed, bulletDamage;

    private WeaponProperties(String displayName, Type type, int levelRequired, int roundsPerMinute, float reloadDelay, int maximumWeaponCapacity,
                             int speedInMetersPerSecond, float bulletsToKillNearTarget) {
        this(BasicWeapon.class, displayName, type, levelRequired, 60f / (float) roundsPerMinute, reloadDelay, maximumWeaponCapacity, ((float) speedInMetersPerSecond / 100f) + BASE_BULLET_SPEED, PlayerEntity.DEFAULT_HEALTH / bulletsToKillNearTarget);
    }

    private WeaponProperties(Class aClass, String displayName, Type type, int levelRequired, float firingDelay, float reloadDelay, int maximumWeaponCapacity,
                             float bulletSpeed, float bulletDamage) {
        this.aClass = aClass;
        this.displayName = displayName;
        this.type = type;
        this.levelRequired = levelRequired;
        this.firingDelay = firingDelay;
        this.reloadDelay = reloadDelay;
        this.maximumWeaponCapacity = maximumWeaponCapacity;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Type getType() {
        return type;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public float getFiringDelay() {
        return firingDelay;
    }

    public float getReloadDelay() {
        return reloadDelay;
    }

    public int getMaximumWeaponCapacity() {
        return maximumWeaponCapacity;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public float getBulletDamage() {
        return bulletDamage;
    }

    public WeaponController toWeaponController(EntityManager entityManager) {
        try {
            Constructor constructor = aClass.getDeclaredConstructor(EntityManager.class, WeaponProperties.class);
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

    public static WeaponProperties valueOf(int ordinal) {
        // Fix ordinal
        if(ordinal >= values().length)
            ordinal = 0;
        else if(ordinal < 0)
            ordinal = values().length - 1;

        for(WeaponProperties weaponProperties : values()) {
            if(weaponProperties.ordinal() == ordinal)
                return weaponProperties;
        }
        return null;
    }

    public static Array<WeaponProperties> randomValues() {
        Array<WeaponProperties> list = new Array<WeaponProperties>();
        list.addAll(values());
        list.shuffle();
        return list;
    }

    public static enum Type {

        ASSAULT_RIFLE(20f, 14f, new short[]{WeaponHolder.WeaponType.PRIMARY.getType()}),
        SUBMACHINE_GUNS(15f, 9.5f, new short[]{WeaponHolder.WeaponType.PRIMARY.getType()}),
        MACHINE_PISTOLS(11f, 7.5f, new short[]{WeaponHolder.WeaponType.SECONDARY.getType()}); // WeaponHolder.WeaponType.PRIMARY.getType()

        private final float maxDistance, effectiveDistance;
        private final short[] type;

        private Type(float maxDistance, float effectiveDistance, short[] type) {
            this.maxDistance = maxDistance;
            this.effectiveDistance = effectiveDistance; // Distance to drop the damage to 50%
            this.type = type;
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
    }
}
