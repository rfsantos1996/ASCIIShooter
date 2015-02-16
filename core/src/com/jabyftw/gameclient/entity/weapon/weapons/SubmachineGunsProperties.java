package com.jabyftw.gameclient.entity.weapon.weapons;

import com.jabyftw.gameclient.entity.weapon.FullyAutomaticWeapon;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;

/**
 * Created by Rafael on 07/02/2015.
 */
@SuppressWarnings("UnusedDeclaration")
public final class SubmachineGunsProperties extends WeaponProperties {

    /*
     * THIS NEEDS TO BE ORDERED (OLDEST ON TOP)!!!
     */
    public static final WeaponProperties MP7 = new SubmachineGunsProperties("MP7", 950, 2.2f, 30, 735, 2.2f);
    public static final WeaponProperties MP9 = new SubmachineGunsProperties("MP9", 1000, 2.7f, 32, 390, 2.4f);
    public static final WeaponProperties UZI = new SubmachineGunsProperties("Uzi", 600, 2.2f, 20, 400, 2.1f);
    public static final WeaponProperties P90 = new SubmachineGunsProperties("P90", 900, 3.0f, 50, 715, 2.5f);
    public static final WeaponProperties PP_90M1 = new SubmachineGunsProperties("PP-90M1", 800, 3.2f, 52, 320, 2.8f);

    private SubmachineGunsProperties(String weaponName, int roundsPerMinute, float reloadTime, int rounds, int bulledSpeed, float bulletsToKillPlayer) {
        super(FullyAutomaticWeapon.class, WeaponType.SUBMACHINE_GUNS, weaponName, roundsPerMinute, reloadTime, rounds, bulledSpeed, bulletsToKillPlayer);
    }

    public static void initialize() {
    }
}
