package com.jabyftw.gameclient.entity.weapon;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.jabyftw.gameclient.util.files.OfflinePlayerProfile;
import com.jabyftw.gameclient.util.files.OnlinePlayerProfile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Isa on 05/01/2015.
 */
public class Layout implements Json.Serializable {

    private final HashMap<WeaponHolder.WeaponType, WeaponProperties> weapons = new HashMap<WeaponHolder.WeaponType, WeaponProperties>();

    private String displayName;

    public Layout(String displayName) {
        this.displayName = displayName;
    }

    public Layout() {
    }

    public void addWeapon(WeaponHolder.WeaponType weaponType, WeaponProperties weaponProperties) {
        weapons.put(weaponType, weaponProperties);
    }

    public HashMap<WeaponHolder.WeaponType, WeaponProperties> getWeaponsMap() {
        return weapons;
    }

    public void validate(OnlinePlayerProfile onlinePlayerProfile) {
        for(WeaponHolder.WeaponType weaponType : WeaponHolder.WeaponType.values()) {
            if(weaponType.isRequired() && weapons.get(weaponType) == null)
                weapons.put(weaponType, null);
        }

        Set<Map.Entry<WeaponHolder.WeaponType, WeaponProperties>> entrySet = weapons.entrySet();
        Iterator<Map.Entry<WeaponHolder.WeaponType, WeaponProperties>> iterator = entrySet.iterator();
        int size = entrySet.size();

        while(iterator.hasNext() && size >= 0) {
            Map.Entry<WeaponHolder.WeaponType, WeaponProperties> entry = iterator.next();
            WeaponHolder.WeaponType key = entry.getKey();
            WeaponProperties value = entry.getValue();

            boolean possible = false;

            if(value != null) {
                short[] possibleTypes = value.getType().getPossibleTypes();
                for(short possibleType : possibleTypes) {
                    if(possibleType == key.getType() && value.getLevelRequired() <= onlinePlayerProfile.getLevel())
                        possible = true;
                }
            }

            if(!possible) {
                if(key.isRequired())
                    entry.setValue(getRandomWeaponProperties(key, onlinePlayerProfile));
                else
                    iterator.remove();
            }
            size--;
        }
    }

    private WeaponProperties getRandomWeaponProperties(WeaponHolder.WeaponType weaponType, OnlinePlayerProfile onlinePlayerProfile) {
        for(WeaponProperties weaponProperties : WeaponProperties.randomValues()) {
            for(short possibleType : weaponProperties.getType().getPossibleTypes()) {
                if(weaponType.getType() == possibleType && weaponProperties.getLevelRequired() <= onlinePlayerProfile.getLevel())
                    return weaponProperties;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void write(Json json) {
        json.writeValue("displayName", displayName);
        json.writeObjectStart("weapons");
        for(Map.Entry<WeaponHolder.WeaponType, WeaponProperties> entry : weapons.entrySet()) {
            json.writeValue(entry.getKey().name(), entry.getValue().name());
        }
        json.writeObjectEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.displayName = jsonData.getString("displayName");
        JsonValue weaponsJson = jsonData.get("weapons");

        int index = 0;
        JsonValue nextWeapon;
        while((nextWeapon = weaponsJson.get(index)) != null) {
            weapons.put(
                    WeaponHolder.WeaponType.valueOf(nextWeapon.name()),
                    WeaponProperties.valueOf(nextWeapon.asString())
            );
            index++;
        }
    }
}
