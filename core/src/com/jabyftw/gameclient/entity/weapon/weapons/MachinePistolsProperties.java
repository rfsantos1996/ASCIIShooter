package com.jabyftw.gameclient.entity.weapon.weapons;

import com.jabyftw.gameclient.entity.weapon.FullyAutomaticWeapon;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;

/**
 * Created by Rafael on 07/02/2015.
 */
@SuppressWarnings("UnusedDeclaration")
public final class MachinePistolsProperties extends WeaponProperties {

    /*
     * THIS NEEDS TO BE ORDERED (OLDEST ON TOP)!!!
     */
    public static final WeaponProperties MAC_11 = new MachinePistolsProperties("MAC-11", 1200, 1.6f, 32, 980, 1.4f);
    public static final WeaponProperties GLOCK_18 = new MachinePistolsProperties("Glock 18", 1150, 2.1f, 33, 375, 1.3f);
    public static final WeaponProperties MINI_UZI = new MachinePistolsProperties("Mini-Uzi", 950, 1.9f, 20, 375, 1.8f);

    private MachinePistolsProperties(String weaponName, int roundsPerMinute, float reloadTime, int rounds, int bulledSpeed, float bulletsToKillPlayer) {
        super(FullyAutomaticWeapon.class, WeaponType.MACHINE_PISTOLS, weaponName, roundsPerMinute, reloadTime, rounds, bulledSpeed, bulletsToKillPlayer);
    }

    public static void initialize() {
    }
}
