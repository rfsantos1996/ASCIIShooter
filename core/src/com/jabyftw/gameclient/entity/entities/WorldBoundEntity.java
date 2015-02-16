package com.jabyftw.gameclient.entity.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jabyftw.gameclient.entity.AbstractBox2dEntity;
import com.jabyftw.gameclient.entity.EntityManager;
import com.jabyftw.gameclient.entity.EntityType;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.util.Constants;

/**
 * Created by Rafael on 14/02/2015.
 */
public class WorldBoundEntity extends AbstractBox2dEntity {

    private Vector2 box2dMaxCoordinates, box2dMinCoordinates;

    public WorldBoundEntity(Long entityId, EntityManager entityManager, Map map, Vector2 spawnLocation) {
        super(entityId, entityManager, map, spawnLocation);
    }

    public void setBox2dBounds(Vector2 box2dMaxCoordinates, Vector2 box2dMinCoordinates) {
        this.box2dMaxCoordinates = box2dMaxCoordinates;
        this.box2dMinCoordinates = box2dMinCoordinates;
    }

    @Override
    public void contactWith(Contact contact, Object objectContactedWith) {
        if(objectContactedWith instanceof Entity && ((Entity) objectContactedWith).getEntityType() == EntityType.BULLET)
            ((Entity) objectContactedWith).remove(false);
    }

    @Override
    public void spawnBox2dBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;

        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(new Vector2[]{
                new Vector2(box2dMinCoordinates.x, box2dMinCoordinates.y),
                new Vector2(box2dMaxCoordinates.x, box2dMinCoordinates.y),
                new Vector2(box2dMaxCoordinates.x, box2dMaxCoordinates.y),
                new Vector2(box2dMinCoordinates.x, box2dMaxCoordinates.y)
        });

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Constants.Box2dConstants.BIT_WORLD_BOUNDS;
        fixtureDef.filter.maskBits = Constants.Box2dConstants.BIT_PLAYER_ENTITY | Constants.Box2dConstants.BIT_ENEMY_ENTITY | Constants.Box2dConstants.BIT_BULLET_ENTITY;
        fixtureDef.shape = chainShape;
        fixtureDef.restitution = 0;
        fixtureDef.friction = 0.6f;
        fixtureDef.density = 3f;

        createBox2dBody(map.getWorld(), bodyDef, fixtureDef);

        chainShape.dispose();
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.WORLD_BOUNDS;
    }
}
