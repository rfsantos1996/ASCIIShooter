package com.jabyftw.gameclient.entity.entities;

import com.badlogic.gdx.math.Vector2;
import com.jabyftw.gameclient.entity.AbstractDamageableEntity;
import com.jabyftw.gameclient.maps.Map;

/**
 * Created by Rafael on 10/01/2015.
 */
public class EnemyPlayer extends AbstractDamageableEntity {

    protected EnemyPlayer(long entityId, EntityManager entityManager, Map map, Vector2 location) {
        super(entityId, entityManager, map, PlayerEntity.DEFAULT_HEALTH);
    }

    @Override
    protected void doOnDeath() {
    }

    @Override
    public void spawnBox2dBody() {

    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    @Override
    public Vector2 getLocation() {
        return null;
    }
}
