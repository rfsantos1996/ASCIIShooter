package com.jabyftw.gameclient.entity.weapon.util;

import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.entity.weapon.WeaponSlotType;
import com.jabyftw.gameclient.util.GameDrawable;
import com.jabyftw.gameclient.util.HudDrawable;
import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Isa on 04/01/2015.
 */
public interface WeaponHolder extends GameDrawable, HudDrawable, Tickable {

    public void addWeaponToHolder(WeaponSlotType weaponSlotType, WeaponController weaponController);

    public void removeWeaponToHolder(WeaponSlotType weaponSlotType);

    public WeaponController getSelectedWeapon();

    public WeaponSlotType[] getAvailableWeaponSlotTypes();

    public WeaponSlotType getSelectedWeaponType();

    public void selectWeaponType(boolean next);

    public boolean isChangingWeapon();

    public void resetStats();

    public Layout getLayout();

}
