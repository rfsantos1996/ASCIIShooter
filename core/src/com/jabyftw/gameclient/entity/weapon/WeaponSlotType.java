package com.jabyftw.gameclient.entity.weapon;

import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

import java.util.HashMap;

/**
 * Created by Rafael on 04/02/2015.
 */
public enum WeaponSlotType {

    PRIMARY((short) 0, LangEnum.PRIMARY_WEAPON),
    SECONDARY((short) 1, LangEnum.SECONDARY_WEAPON),
    THROWABLE_1((short) 2, LangEnum.THROWABLE_WEAPON, false),
    THROWABLE_2((short) 2, LangEnum.THROWABLE_WEAPON, false);

    private final short type;
    private final LangEnum slotName;
    private final boolean required;

    private WeaponSlotType(short type, LangEnum slotName) {
        this(type, slotName, true);
    }

    private WeaponSlotType(short type, LangEnum slotName, boolean required) {
        this.type = type;
        this.slotName = slotName;
        this.required = required;
    }

    public short getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDisplayName() {
        return Resources.getLang(slotName);
    }

    public static WeaponSlotType valueOf(int ordinal) {
        // Fix ordinal
        if(ordinal >= values().length)
            ordinal = 0;
        else if(ordinal < 0)
            ordinal = values().length - 1;

        for(WeaponSlotType weaponSlotType : values()) {
            if(weaponSlotType.ordinal() == ordinal)
                return weaponSlotType;
        }
        return null;
    }

    public static WeaponSlotType[] valueOf(short type) {
        Array<WeaponSlotType> validTypes = new Array<WeaponSlotType>();

        for(WeaponSlotType weaponSlotType : values()) {
            if(weaponSlotType.getType() == type) validTypes.add(weaponSlotType);
        }

        return validTypes.toArray();
    }

    public static WeaponSlotType[] uniqueTypesValues() {
        HashMap<Short, WeaponSlotType> weaponTypeMap = new HashMap<Short, WeaponSlotType>();

        for(WeaponSlotType weaponSlotType : values()) {
            if(!weaponTypeMap.containsKey(weaponSlotType.getType())) // Just insert if not the same deliverType
                weaponTypeMap.put(weaponSlotType.getType(), weaponSlotType);
        }

        return weaponTypeMap.values().toArray(new WeaponSlotType[weaponTypeMap.size()]);
    }
}
