package com.jabyftw.gameclient.entity.util;

/**
 * Created by Isa on 03/01/2015.
 */
public interface DamageableEntity extends Entity {

    public boolean doDamage(float damage, boolean ignoreIfIsHittable);

    public void doHeal(float amount);

    public float getTimeInvincible();

    public void setInvincible(float seconds);

    public boolean isHittable();

    public float getRemainingHealth();

    public float getMaximumHealth();

}
