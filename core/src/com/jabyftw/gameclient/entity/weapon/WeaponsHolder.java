package com.jabyftw.gameclient.entity.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.entity.weapon.util.WeaponController;
import com.jabyftw.gameclient.entity.weapon.util.WeaponHolder;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Isa on 04/01/2015.
 */
public class WeaponsHolder implements WeaponHolder {

    private final HashMap<WeaponSlotType, WeaponController> weapons = new HashMap<WeaponSlotType, WeaponController>();
    private final Entity holder;
    private final Layout layout;

    private WeaponSlotType[] availableWeaponSlotTypes;
    private DisplayText changingDisplayText = null;
    private int selected = 0;
    private float changeTime = 0;
    private long lastChangeTick = 0;

    public WeaponsHolder(Entity holder, Layout layout) {
        this.holder = holder;
        this.layout = layout;
        for(Map.Entry<WeaponSlotType, WeaponProperties> entry : layout.getWeaponsMap().entrySet()) {
            if(entry.getValue() != null)
                addWeaponToHolder(entry.getKey(), entry.getValue().toWeaponController(holder.getEntityManager()));
        }
    }

    @Override
    public void update(float deltaTime) {
        if(isChangingWeapon()) {
            changeTime -= deltaTime;
        } else {
            changeTime = 0;
            getSelectedWeapon().update(deltaTime);
        }
    }

    @Override
    public void drawGame(SpriteBatch batch) {
        if(Main.getTicksPassed() - lastChangeTick <= 1) {
            if(changingDisplayText != null)
                changingDisplayText.dispose();
            changingDisplayText = new DisplayText(holder, FontEnum.PRESS_START_14, getSelectedWeapon().getDisplayName(), 1.5f);
        }
        getSelectedWeapon().drawGame(batch, holder);
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        getSelectedWeapon().drawHUD(batch, holder);
    }

    @Override
    public void addWeaponToHolder(WeaponSlotType weaponSlotType, WeaponController weaponController) {
        weapons.put(weaponSlotType, weaponController);
        availableWeaponSlotTypes = getAvailableWeaponSlotTypes();
    }

    @Override
    public void removeWeaponToHolder(WeaponSlotType weaponSlotType) {
        weapons.remove(weaponSlotType);
        availableWeaponSlotTypes = getAvailableWeaponSlotTypes();
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    @Override
    public boolean isChangingWeapon() {
        return changeTime > 0;
    }

    @Override
    public WeaponSlotType getSelectedWeaponType() {
        return availableWeaponSlotTypes[selected];
    }

    @Override
    public WeaponController getSelectedWeapon() {
        return weapons.get(availableWeaponSlotTypes[selected]);
    }

    @Override
    public WeaponSlotType[] getAvailableWeaponSlotTypes() {
        WeaponSlotType[] weaponSlotTypes = new WeaponSlotType[weapons.size()];
        int lastIndex = 0;
        for(WeaponSlotType weaponSlotType : WeaponSlotType.values()) { // Keep the order!!
            if(weapons.get(weaponSlotType) != null)
                weaponSlotTypes[lastIndex++] = weaponSlotType;
        }
        return weaponSlotTypes;
    }

    @Override
    public void selectWeaponType(boolean next) {
        int newSelected = selected + (next ? 1 : -1);

        if(newSelected >= availableWeaponSlotTypes.length)
            newSelected = 0;
        else if(newSelected < 0)
            newSelected = availableWeaponSlotTypes.length - 1;

        getSelectedWeapon().doOnChangeWeapon();
        selected = newSelected;
        changeTime = Constants.Gameplay.Player.WEAPON_CHANGE_TIME;
        lastChangeTick = Main.getTicksPassed();
    }

    @Override
    public void resetStats() {
        for(WeaponController weapon : weapons.values()) {
            weapon.resetStats();
        }
    }
}
