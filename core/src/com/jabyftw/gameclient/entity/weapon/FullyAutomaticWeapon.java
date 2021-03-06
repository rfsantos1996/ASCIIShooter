package com.jabyftw.gameclient.entity.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jabyftw.gameclient.entity.EntityManager;
import com.jabyftw.gameclient.entity.EntityType;
import com.jabyftw.gameclient.entity.entities.BulletEntity;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.entity.weapon.util.WeaponController;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.FontEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

import java.text.DecimalFormat;

/**
 * Created by Isa on 03/01/2015.
 */
public class FullyAutomaticWeapon implements WeaponController {

    private final EntityManager entityManager;
    private final WeaponProperties weaponProperties;

    private int currentWeaponCapacity;
    private float elapsedFireTime = 0;
    private float elapsedReloadTime = 0;
    private float reloadTimeMultiplier = 1;

    private boolean askedToReload = false;

    private DisplayText reloadDisplayText = null;

    public FullyAutomaticWeapon(EntityManager entityManager, WeaponProperties weaponProperties) {
        this.entityManager = entityManager;
        this.weaponProperties = weaponProperties;
        this.currentWeaponCapacity = weaponProperties.getMaximumWeaponCapacity();
    }

    @Override
    public void update(float deltaTime) {
        setElapsedFireTime(Math.min(getElapsedFireTime(), getFiringDelay()) + deltaTime);

        // If wants to reload or is reloading
        if((askedToReload || isReloading()) && getMaximumWeaponCapacity() - getCurrentWeaponCapacity() > 0) {
            // Add elapsed reload time
            setElapsedReloadTime(getElapsedReloadTime() + (deltaTime * getReloadTimeMultiplier()));
            askedToReload = false;

            // Finished reloading
            if(getElapsedReloadTime() >= getReloadDelay()) {
                // full the weapon capacity and stop reloading
                setCurrentWeaponCapacity(getMaximumWeaponCapacity());
                setElapsedReloadTime(0);
            }
        }
    }

    @Override
    public boolean fire(float deltaTime, Entity owner, Vector2 location, float angle) {
        // Has bullets in weapon capacity
        if(getCurrentWeaponCapacity() > 0) {
            // And is ready to fire
            while(isReadyToFire()) {
                // Reset elapsed fire time and reload time, spend one bullet and then spawn bullet
                setElapsedFireTime(getElapsedFireTime() - getFiringDelay());
                setElapsedReloadTime(0);
                setCurrentWeaponCapacity(getCurrentWeaponCapacity() - 1);
                spawnBullet(location, angle, owner, weaponProperties);
            }
            return true;
        } else if(!isReloading()) { // doesn't have bullets and isn't reload, ask to reload
            askToReload();
        }
        return false;
    }

    @Override
    public void drawGame(SpriteBatch batch, Entity holder) {
        float reloadRatio = getElapsedReloadTime() * 100f / getReloadDelay();
        String renderingText = isReloading() ?
                Resources.getLang(LangEnum.RELOADING_INFO)
                        .replaceAll("%percentage%", Util.formatDecimal(reloadRatio > 100 ? 100 : reloadRatio, 0)) :
                Resources.getLang(LangEnum.RELOADING_HINT);

        if(isReloading() || ((float) getCurrentWeaponCapacity() / (float) getMaximumWeaponCapacity()) <= 0.2f) {
            if(reloadDisplayText != null)
                reloadDisplayText.dispose();
            reloadDisplayText = new DisplayText(holder, renderingText);
        }
    }

    @Override
    public void drawHUD(SpriteBatch batch, Entity entityHoldingWeapon) {
        BitmapFont font = Resources.getBitmapFont(FontEnum.PRESS_START_28);

        String firstString = Resources.getLang(LangEnum.CURRENT_WEAPON_CAPACITY)
                .replaceAll("%currentweaponcapacity%", new DecimalFormat((getMaximumWeaponCapacity() >= 100 ? "0" : "") + "00").format(getCurrentWeaponCapacity()));

        Util.drawText(
                font,
                batch,
                firstString,
                getCurrentCapacityColor(),
                (font.getSpaceWidth() / 2f),
                (2 * font.getLineHeight() / 3f)
        );

        Util.drawText(
                font,
                batch,
                Resources.getLang(LangEnum.MAXIMUM_WEAPON_CAPACITY)
                        .replaceAll("%maximumweaponcapacity%", String.valueOf(getMaximumWeaponCapacity())),
                (font.getSpaceWidth() / 2f) + (firstString.length() * font.getSpaceWidth()),
                (2 * font.getLineHeight() / 3f)
        );
    }

    @Override
    public Entity spawnBullet(Vector2 location, float angle, Entity owner, WeaponProperties weaponProperties) {
        BulletEntity bullet = (BulletEntity) entityManager.spawnEntity(
                EntityType.BULLET,
                location
        );
        bullet.setProperties(owner, angle, weaponProperties);
        return bullet;
    }

    @Override
    public void doOnChangeWeapon() {
        elapsedFireTime = 0;
        elapsedReloadTime = 0;
        reloadTimeMultiplier = 1;
        askedToReload = false;
    }

    @Override
    public void resetStats() {
        currentWeaponCapacity = getMaximumWeaponCapacity();
        doOnChangeWeapon();
    }

    @Override
    public WeaponProperties getWeaponProperties() {
        return weaponProperties;
    }

    @Override
    public String getDisplayName() {
        return weaponProperties.getWeaponName();
    }

    @Override
    public WeaponController copy() {
        return new FullyAutomaticWeapon(entityManager, weaponProperties);
    }

    private Color getCurrentCapacityColor() {
        float ratio = (float) getCurrentWeaponCapacity() / (float) getMaximumWeaponCapacity();
        if(ratio > 0.35f)
            return Color.WHITE;
        else if(ratio > 0.2f)
            return Color.ORANGE;
        else
            return Color.RED;
    }

    @Override
    public float getReloadTimeMultiplier() {
        return reloadTimeMultiplier;
    }

    @Override
    public void setReloadTimeMultiplier(float multiplier) {
        if(multiplier < 0) multiplier = 0;
        this.reloadTimeMultiplier = multiplier;
    }

    @Override
    public void askToReload() {
        askedToReload = true;
    }

    @Override
    public boolean isReloading() {
        return elapsedReloadTime > 0;
    }

    @Override
    public boolean isReadyToFire() {
        return elapsedFireTime >= getFiringDelay() && currentWeaponCapacity > 0;
    }

    @Override
    public float getFiringDelay() {
        return weaponProperties.getFiringDelay();
    }

    @Override
    public float getReloadDelay() {
        return weaponProperties.getReloadDelay();
    }

    @Override
    public float getElapsedFireTime() {
        return this.elapsedFireTime;
    }

    @Override
    public void setElapsedFireTime(float elapsedFireTime) {
        this.elapsedFireTime = elapsedFireTime;
    }

    @Override
    public float getElapsedReloadTime() {
        return this.elapsedReloadTime;
    }

    @Override
    public void setElapsedReloadTime(float elapsedReloadTime) {
        this.elapsedReloadTime = elapsedReloadTime;
    }

    @Override
    public int getMaximumWeaponCapacity() {
        return weaponProperties.getMaximumWeaponCapacity();
    }

    @Override
    public int getCurrentWeaponCapacity() {
        return currentWeaponCapacity;
    }

    @Override
    public void setCurrentWeaponCapacity(int newWeaponCapacity) {
        this.currentWeaponCapacity = newWeaponCapacity;
        if(currentWeaponCapacity > getMaximumWeaponCapacity())
            this.currentWeaponCapacity = getMaximumWeaponCapacity();
        else if(currentWeaponCapacity < 0)
            this.currentWeaponCapacity = 0;
    }
}
