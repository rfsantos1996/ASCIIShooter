package com.jabyftw.gameclient.entity;

import com.jabyftw.gameclient.entity.entities.*;
import com.jabyftw.gameclient.entity.util.Entity;

/**
 * Created by Rafael on 14/12/2014.
 */
public enum EntityType {

    WORLD_BOUNDS("World bounds", WorldBoundEntity.class),
    BULLET("Bullet", BulletEntity.class),
    TARGET("Target", TargetEntity.class),
    PLAYER("Player", PlayerEntity.class),
    PLAYER_ENEMY("Enemy player", EnemyEntity.class);

    private final String name;
    private final Class<? extends Entity> clazz;

    private EntityType(String name, Class<? extends Entity> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Entity> getClazz() {
        return clazz;
    }

    public static EntityType[] drawOrderArray() {
        return new EntityType[]{
                BULLET,
                TARGET,
                PLAYER,
                PLAYER_ENEMY
        };
    }
}
