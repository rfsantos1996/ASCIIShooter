package com.jabyftw.gameclient.entity.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.util.files.enums.FontEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Isa on 04/01/2015.
 */
public class WeaponsHolder implements WeaponHolder {

    public static final float DEFAULT_CHANGE_TIME = 0.7f;

    private final Entity holder;
    private final Layout layout;
    private final HashMap<WeaponType, WeaponController> weapons = new HashMap<WeaponType, WeaponController>();

    private WeaponType[] availableWeaponTypes;
    private DisplayText changingDisplayText = null;
    private int selected = 0;
    private float changeTime = 0;
    private long lastChangeTick = 0;

    public WeaponsHolder(Entity holder, Layout layout) {
        this.holder = holder;
        this.layout = layout;
        for(Map.Entry<WeaponType, WeaponProperties> entry : layout.getWeaponsMap().entrySet()) {
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
    public void draw(SpriteBatch batch) {
        if(Main.getTicksPassed() - lastChangeTick <= 1) {
            if(changingDisplayText != null)
                changingDisplayText.dispose();
            changingDisplayText = new DisplayText(holder, FontEnum.PRESS_START_14, getSelectedWeapon().getDisplayName(), 1.5f);
        }
        getSelectedWeapon().draw(batch, holder);
    }

    @Override
    public void addWeaponToHolder(WeaponType weaponType, WeaponController weaponController) {
        weapons.put(weaponType, weaponController);
        availableWeaponTypes = getAvailableWeaponTypes();
    }

    @Override
    public void removeWeaponToHolder(WeaponType weaponType) {
        weapons.remove(weaponType);
        availableWeaponTypes = getAvailableWeaponTypes();
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
    public WeaponType getSelectedWeaponType() {
        return availableWeaponTypes[selected];
    }

    @Override
    public WeaponController getSelectedWeapon() {
        return weapons.get(availableWeaponTypes[selected]);
    }

    @Override
    public WeaponType[] getAvailableWeaponTypes() {
        WeaponType[] weaponTypes = new WeaponType[weapons.size()];
        int lastIndex = 0;
        for(WeaponType weaponType : WeaponType.values()) { // Keep the order!!
            if(weapons.get(weaponType) != null)
                weaponTypes[lastIndex++] = weaponType;
        }
        return weaponTypes;
    }

    @Override
    public void selectWeaponType(boolean next) {
        int newSelected = selected + (next ? 1 : -1);

        if(newSelected >= availableWeaponTypes.length)
            newSelected = 0;
        else if(newSelected < 0)
            newSelected = availableWeaponTypes.length - 1;

        getSelectedWeapon().doOnChangeWeapon();
        selected = newSelected;
        changeTime = DEFAULT_CHANGE_TIME;
        lastChangeTick = Main.getTicksPassed();
    }

    @Override
    public void resetStats() {
        for(WeaponController weapon : weapons.values()) {
            weapon.resetStats();
        }
    }
}
