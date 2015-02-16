package com.jabyftw.gameclient.entity.weapon;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;
import com.jabyftw.gameclient.entity.weapon.weapons.WeaponType;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Isa on 05/01/2015.
 */
public class Layout implements Json.Serializable {

    private final HashMap<WeaponSlotType, WeaponProperties> weapons = new HashMap<WeaponSlotType, WeaponProperties>();
    private int index;
    private String displayName;

    public Layout(int index) {
        this.index = index;
        setDisplayName(null);
    }

    public Layout() {
    }

    public void setWeapon(WeaponSlotType weaponSlotType, WeaponProperties weaponProperties) {
        weapons.put(weaponSlotType, weaponProperties);
    }

    public WeaponProperties getWeapon(WeaponSlotType weaponSlotType) {
        return weapons.get(weaponSlotType);
    }

    public HashMap<WeaponSlotType, WeaponProperties> getWeaponsMap() {
        return weapons;
    }

    public boolean validate(OnlinePlayerProfile onlinePlayerProfile) {
        boolean changed = false;

        for(WeaponSlotType weaponSlotType : WeaponSlotType.values()) {
            if(weaponSlotType.isRequired() && !weapons.containsKey(weaponSlotType)) {
                weapons.put(weaponSlotType, null);
                changed = true;
            }
        }

        Set<Map.Entry<WeaponSlotType, WeaponProperties>> entrySet = weapons.entrySet();
        Iterator<Map.Entry<WeaponSlotType, WeaponProperties>> iterator = entrySet.iterator();
        int size = entrySet.size();

        while(iterator.hasNext() && size >= 0) {
            Map.Entry<WeaponSlotType, WeaponProperties> entry = iterator.next();

            WeaponSlotType key = entry.getKey();
            WeaponProperties value = entry.getValue();

            boolean possible = false;

            if(value != null) {
                short[] possibleTypes = value.getWeaponType().getPossibleTypes();
                for(short possibleType : possibleTypes) {
                    if(possibleType == key.getType() && value.getLevelRequired() <= onlinePlayerProfile.getLevel())
                        possible = true;
                }
            }

            if(!possible) {
                if(key.isRequired()) {
                    WeaponProperties randomWeaponProperties = getRandomWeaponProperties(key, onlinePlayerProfile);
                    if(entry.getValue() != randomWeaponProperties) {
                        changed = true;
                        entry.setValue(randomWeaponProperties);
                    }
                } else {
                    iterator.remove();
                    changed = true;
                }
            }

            size--;
        }

        return changed;
    }

    private WeaponProperties getRandomWeaponProperties(WeaponSlotType weaponSlotType, OnlinePlayerProfile onlinePlayerProfile) {
        Array<WeaponProperties> values = WeaponProperties.values(weaponSlotType.getType());

        if(values.size == 0)
            throw new IllegalArgumentException("There are no weapons for " + weaponSlotType.name() + " and it is required.");

        WeaponProperties lowestProperties = null;
        int lowestLevel = Integer.MAX_VALUE;

        for(WeaponProperties value : values) {
            int levelsRequired = value.getLevelRequired() - onlinePlayerProfile.getLevel();

            if(levelsRequired < lowestLevel) {
                lowestProperties = value;
                lowestLevel = levelsRequired;
            }
        }

        return lowestProperties;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean setDisplayName(String displayName) {
        if(displayName == null || displayName.length() <= Constants.Gameplay.TEXT_TOO_SHORT || displayName.length() > Constants.Gameplay.TEXT_TOO_LONG) {
            this.displayName = "Layout " + (index + 1);
            return false;
        }
        this.displayName = displayName;
        return true;
    }

    @Override
    public void write(Json json) {
        json.writeValue("index", index);
        json.writeValue("displayName", displayName);
        {
            json.writeArrayStart("weapons");
            for(Map.Entry<WeaponSlotType, WeaponProperties> entry : weapons.entrySet()) {
                json.writeValue(new WeaponStorage(entry.getKey(), entry.getValue()), WeaponStorage.class);
            }
            json.writeArrayEnd();
        }
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.index = jsonData.getInt("index");
        this.displayName = jsonData.getString("displayName");
        {
            JsonValue weaponsJson = jsonData.get("weapons");

            int index = 0;
            JsonValue nextWeapon;
            while((nextWeapon = weaponsJson.get(index)) != null) {

                WeaponStorage weaponStorage = new WeaponStorage();
                weaponStorage.read(json, nextWeapon);

                weapons.put(
                        weaponStorage.getWeaponSlotType(),
                        weaponStorage.getWeaponProperties()
                );
                index++;
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public class WeaponStorage implements Json.Serializable {

        private int weaponSlotTypeOrdinal, weaponPropertiesOrdinal, weaponTypeOrdinal;

        public WeaponStorage(WeaponSlotType weaponSlotType, WeaponProperties weaponProperties) {
            setWeaponSlotType(weaponSlotType);
            setWeaponProperties(weaponProperties);
        }

        public WeaponStorage() {
        }

        public WeaponProperties getWeaponProperties() {
            return WeaponProperties.valueOf(WeaponType.valueOf(weaponTypeOrdinal), weaponPropertiesOrdinal);
        }

        public WeaponSlotType getWeaponSlotType() {
            return WeaponSlotType.valueOf(weaponSlotTypeOrdinal);
        }

        public void setWeaponProperties(WeaponProperties weaponProperties) {
            this.weaponTypeOrdinal = weaponProperties.getWeaponType().ordinal();
            this.weaponPropertiesOrdinal = weaponProperties.ordinal();
        }

        public void setWeaponSlotType(WeaponSlotType weaponSlotType) {
            this.weaponSlotTypeOrdinal = weaponSlotType.ordinal();
        }

        @Override
        public void write(Json json) {
            json.writeValue("weaponSlotType", weaponSlotTypeOrdinal, Integer.class);
            json.writeValue("weaponProperties", weaponPropertiesOrdinal, Integer.class);
            json.writeValue("weaponType", weaponTypeOrdinal, Integer.class);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            this.weaponSlotTypeOrdinal = jsonData.getInt("weaponSlotType");
            this.weaponPropertiesOrdinal = jsonData.getInt("weaponProperties");
            this.weaponTypeOrdinal = jsonData.getInt("weaponType");
        }
    }
}
