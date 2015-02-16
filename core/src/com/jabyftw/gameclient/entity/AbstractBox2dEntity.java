package com.jabyftw.gameclient.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.jabyftw.gameclient.entity.util.Box2dEntity;
import com.jabyftw.gameclient.maps.Map;

/**
 * Created by Rafael on 20/01/2015.
 */
public abstract class AbstractBox2dEntity extends AbstractEntity implements Box2dEntity {

    protected Body box2dBody;

    protected AbstractBox2dEntity(long entityId, EntityManager entityManager, Map map, Vector2 spawnLocation) {
        super(entityId, entityManager, map, spawnLocation);
    }

    protected void createBox2dBody(World world, BodyDef bodyDef, FixtureDef... fixtureDefs) {
        if(box2dBody != null)
            removeBox2dBody();

        box2dBody = world.createBody(bodyDef);

        for(FixtureDef fixtureDef : fixtureDefs) {
            box2dBody.createFixture(fixtureDef).setUserData(this);
        }
    }

    protected void removeBox2dBody() {
        if(box2dBody != null) {
            map.getWorld().destroyBody(box2dBody);
            box2dBody = null;
        }
    }

    @Override
    protected void doRemoveEntity() {
        removeBox2dBody();
        super.doRemoveEntity();
    }

    @Override
    public Vector2 getLocation() {
        return box2dBody.getPosition();
    }
}
