package com.jabyftw.gameclient.entity.entities;

/**
 * Created by Rafael on 14/12/2014.
 */
public enum EntityType {

    BULLET("Bullet"),
    TARGET("Target"),
    PLAYER("Player"),
    PLAYER_ENEMY("Enemy player");

    private final String name;

    EntityType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EntityType[] drawOrderArray() {
        return new EntityType[] {
                BULLET,
                TARGET,
                PLAYER
        };
    }
}
