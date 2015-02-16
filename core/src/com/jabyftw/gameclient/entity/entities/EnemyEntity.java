package com.jabyftw.gameclient.entity.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.jabyftw.gameclient.entity.AbstractDamageableEntity;
import com.jabyftw.gameclient.entity.EntityManager;
import com.jabyftw.gameclient.entity.EntityType;
import com.jabyftw.gameclient.maps.Map;

/**
 * Created by Rafael on 10/01/2015.
 */
public class EnemyEntity extends AbstractDamageableEntity {

    protected EnemyEntity(Long entityId, EntityManager entityManager, Map map, Vector2 spawnLocation) {
        super(entityId, entityManager, map, spawnLocation);
    }

    @Override
    protected void doOnDeath() {
    }

    @Override
    public void contactWith(Contact contact, Object objectContactedWith) {
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
