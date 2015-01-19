package com.jabyftw.gameclient.entity;

import com.badlogic.gdx.graphics.Color;
import com.jabyftw.gameclient.entity.entities.EntityManager;
import com.jabyftw.gameclient.entity.util.Damageable;
import com.jabyftw.gameclient.maps.Map;

/**
 * Created by Isa on 03/01/2015.
 */
public abstract class AbstractDamageableEntity extends AbstractEntity implements Damageable {

    // Static defaults
    protected static final float DEFAULT_INVINCIBILITY_TIME_AFTER_DAMAGE = 0.125f, DEFAULT_INVINCIBILITY_TIME_AFTER_SPAWN = 2f;

    // Defaults
    protected final float MAXIMUM_HEALTH;

    // variables
    protected float health;
    protected float invincibilityTime = DEFAULT_INVINCIBILITY_TIME_AFTER_SPAWN;
    protected float elapsedTimeSinceDamage = 0;

    protected AbstractDamageableEntity(long entityId, EntityManager entityManager, Map map, float maximumHealth) {
        super(entityId, entityManager, map);
        this.MAXIMUM_HEALTH = maximumHealth;
        this.health = MAXIMUM_HEALTH;
    }

    @Override
    public void update(float deltaTime) {
        elapsedTimeSinceDamage += deltaTime;
        if(invincibilityTime > 0)
            invincibilityTime -= deltaTime;
        if(invincibilityTime < 0)
            invincibilityTime = 0;
        if(health <= 0)
            doOnDeath();
        super.update(deltaTime);
    }

    protected abstract void doOnDeath();

    @Override
    public boolean isHittable() {
        return invincibilityTime <= 0;
    }

    @Override
    public void doDamage(float damage) {
        if(isHittable()) {
            health -= damage;
            if(health > MAXIMUM_HEALTH)
                health = MAXIMUM_HEALTH;
            if(invincibilityTime < 0)
                invincibilityTime = 0;
            invincibilityTime += DEFAULT_INVINCIBILITY_TIME_AFTER_DAMAGE;
            elapsedTimeSinceDamage = 0;
        }
    }

    public Color getDamageFilterColor() {
        return baseColor.cpy().lerp(Color.RED, 1 - Math.min(1, elapsedTimeSinceDamage / invincibilityTime));
    }

    @Override
    public float getTimeInvincible() {
        return invincibilityTime;
    }

    @Override
    public void setInvincible(float timeInvincible) {
        this.invincibilityTime = timeInvincible;
    }

    @Override
    public float getRemainingHealth() {
        return health;
    }

    @Override
    public float getMaximumHealth() {
        return MAXIMUM_HEALTH;
    }
}
