package com.jabyftw.gameclient.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.jabyftw.gameclient.entity.util.DamageableEntity;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.util.Constants;

/**
 * Created by Isa on 03/01/2015.
 */
public abstract class AbstractDamageableEntity extends AbstractBox2dEntity implements DamageableEntity {

    protected float MAXIMUM_HEALTH = Constants.Gameplay.Entities.DEFAULT_HEALTH;
    protected float INVINCIBILITY_AFTER_SPAWN = Constants.Gameplay.Entities.DEFAULT_INVINCIBILITY_TIME_AFTER_SPAWN;
    protected float INVINCIBILITY_AFTER_DAMAGE = Constants.Gameplay.Entities.DEFAULT_INVINCIBILITY_TIME_AFTER_DAMAGE;

    protected float currentHealth;
    protected float elapsedTimeSinceDamage = 0;

    private float invincibilityTime;

    protected AbstractDamageableEntity(long entityId, EntityManager entityManager, Map map, Vector2 spawnLocation) {
        super(entityId, entityManager, map, spawnLocation);
        this.currentHealth = MAXIMUM_HEALTH;
        this.invincibilityTime = INVINCIBILITY_AFTER_SPAWN;
    }

    @Override
    public void update(float deltaTime) {
        elapsedTimeSinceDamage += deltaTime;

        if(invincibilityTime > 0)
            invincibilityTime -= deltaTime;
        if(invincibilityTime < 0)
            invincibilityTime = 0;

        if(currentHealth <= 0)
            doOnDeath();

        super.update(deltaTime);
    }

    protected void doOnDeath() {
        removeBox2dBody();
    }

    @Override
    public boolean isHittable() {
        return invincibilityTime <= 0;
    }

    @Override
    public boolean doDamage(float damage, boolean ignoreIfIsHittable) {
        if(damage < 0)
            throw new IllegalArgumentException("Can't deal negative damage.");

        if(isHittable() || ignoreIfIsHittable) {
            currentHealth -= damage;

            //if(currentHealth > MAXIMUM_HEALTH)
            //currentHealth = MAXIMUM_HEALTH;

            if(invincibilityTime < 0)
                invincibilityTime = 0;

            invincibilityTime = INVINCIBILITY_AFTER_DAMAGE;
            elapsedTimeSinceDamage = 0;

            return true;
        }

        return false;
    }

    @Override
    public void doHeal(float amount) {
        if(amount < 0)
            throw new IllegalArgumentException("Can't heal negative amounts.");
        currentHealth += amount;
    }

    protected Color getDamageFilterColor() {
        return baseColor.cpy().lerp(Color.RED, 1 - Math.min(1, elapsedTimeSinceDamage / invincibilityTime));
    }

    @Override
    public float getTimeInvincible() {
        return invincibilityTime;
    }

    @Override
    public void setInvincible(float timeInvincible) {
        if(timeInvincible < 0)
            throw new IllegalArgumentException("Can't set entity invincible for negative time.");
        this.invincibilityTime = timeInvincible;
    }

    @Override
    public float getRemainingHealth() {
        return currentHealth;
    }

    @Override
    public float getMaximumHealth() {
        return MAXIMUM_HEALTH;
    }
}
