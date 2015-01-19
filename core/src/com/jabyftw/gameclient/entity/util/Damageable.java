package com.jabyftw.gameclient.entity.util;

/**
 * Created by Isa on 03/01/2015.
 */
public interface Damageable extends Entity {

    public void doDamage(float damage);

    public boolean isHittable();

    public float getTimeInvincible();

    public void setInvincible(float seconds);

    public float getRemainingHealth();

    public float getMaximumHealth();
    
}
