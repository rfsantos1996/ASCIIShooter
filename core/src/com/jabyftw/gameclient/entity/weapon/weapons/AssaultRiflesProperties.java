package com.jabyftw.gameclient.entity.weapon.weapons;

import com.jabyftw.gameclient.entity.weapon.FullyAutomaticWeapon;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;

/**
 * Created by Rafael on 07/02/2015.
 */
@SuppressWarnings("UnusedDeclaration")
public final class AssaultRiflesProperties extends WeaponProperties {

    /*
     * THIS NEEDS TO BE ORDERED (OLDEST ON TOP)!!!
     */
    public static final WeaponProperties STG_44 = new AssaultRiflesProperties("StG 44", 575, 3.1f, 30, 685, 3.7f);
    public static final WeaponProperties AUG = new AssaultRiflesProperties("AUG", 705, 2.6f, 32, 970, 3.1f);
    public static final WeaponProperties FAMAS_F1 = new AssaultRiflesProperties("Famas F1", 950, 2.4f, 25, 960, 3.2f);
    public static final WeaponProperties FAMAS_G2 = new AssaultRiflesProperties("Famas G1", 1050, 2.6f, 30, 925, 2.8f);
    public static final WeaponProperties L85A2 = new AssaultRiflesProperties("L85A2", 695, 2.3f, 30, 940, 3.1f);
    public static final WeaponProperties GALIL_ACE = new AssaultRiflesProperties("Galil ACE", 700, 2.6f, 35, 710, 3.3f);
    public static final WeaponProperties SCAR_L = new AssaultRiflesProperties("SCAR-L", 625, 2.1f, 20, 875, 2.9f);
    public static final WeaponProperties AK_47 = new AssaultRiflesProperties("AK-47", 600, 2.4f, 30, 715, 3.3f);
    public static final WeaponProperties M16 = new AssaultRiflesProperties("M16", 815, 2.8f, 20, 985, 3.1f);

    private AssaultRiflesProperties(String weaponName, int roundsPerMinute, float reloadTime, int rounds, int bulledSpeed, float bulletsToKillPlayer) {
        super(FullyAutomaticWeapon.class, WeaponType.ASSAULT_RIFLE, weaponName, roundsPerMinute, reloadTime, rounds, bulledSpeed, bulletsToKillPlayer);
    }

    public static void initialize() {
    }
}
