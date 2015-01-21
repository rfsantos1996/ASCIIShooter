package com.jabyftw.gameclient.entity.weapon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Isa on 03/01/2015.
 */
public interface WeaponController extends Tickable {

    public void draw(SpriteBatch batch, Entity entityHoldingWeapon);

    public boolean fire(float deltaTime, Entity owner, Vector2 location, float angle);

    public Entity spawnBullet(Vector2 location, float angle, Entity owner, WeaponProperties weaponProperties);

    public float getFiringDelay();

    public void askToReload();

    public boolean isReloading();

    public float getReloadDelay();

    public boolean isReadyToFire();

    public float getReloadTimeMultiplier();

    public void setReloadTimeMultiplier(float multiplier);

    public int getMaximumWeaponCapacity();

    public int getCurrentWeaponCapacity();

    public void setCurrentWeaponCapacity(int newWeaponCapacity);

    public float getElapsedFireTime();

    public void setElapsedFireTime(float elapsedFireTime);

    public float getElapsedReloadTime();

    public void setElapsedReloadTime(float elapsedReloadTime);

    public void resetStats();

    public WeaponController copy();

    public String getDisplayName();

    public void doOnChangeWeapon();

    public WeaponProperties getWeaponProperties();
}
