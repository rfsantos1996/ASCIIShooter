package com.jabyftw.gameclient.entity.weapon;

import com.jabyftw.gameclient.util.Drawable;
import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Isa on 04/01/2015.
 */
public interface WeaponHolder extends Drawable, Tickable {

    public void addWeaponToHolder(WeaponsHolder.WeaponType weaponType, WeaponController weaponController);

    public void removeWeaponToHolder(WeaponsHolder.WeaponType weaponType);

    public WeaponController getSelectedWeapon();

    public WeaponType[] getAvailableWeaponTypes();

    public WeaponsHolder.WeaponType getSelectedWeaponType();

    public void selectWeaponType(boolean next);

    public boolean isChangingWeapon();

    public void resetStats();

    public Layout getLayout();

    public enum WeaponType {

        PRIMARY((short) 0),
        SECONDARY((short) 1),
        THROWABLE_1((short) 2, false),
        THROWABLE_2((short) 2, false);

        private final short type;
        private final boolean required;

        private WeaponType(short type) {
            this(type, true);
        }

        private WeaponType(short type, boolean required) {
            this.type = type;
            this.required = required;
        }

        public short getType() {
            return type;
        }

        public boolean isRequired() {
            return required;
        }

        public static WeaponType valueOf(int ordinal) {
            // Fix ordinal
            if(ordinal >= values().length)
                ordinal = 0;
            else if(ordinal < 0)
                ordinal = values().length - 1;

            for(WeaponType weaponType : values()) {
                if(weaponType.ordinal() == ordinal)
                    return weaponType;
            }
            return null;
        }
    }
}
