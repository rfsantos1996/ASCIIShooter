package com.jabyftw.gameclient.entity.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.entity.util.MapViewer;
import com.jabyftw.gameclient.gamestates.play.PlayState;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.maps.util.BlockOpacity;
import com.jabyftw.gameclient.maps.util.Mappable;
import com.jabyftw.gameclient.util.Tickable;

import java.util.Iterator;

/**
 * Created by Isa on 01/01/2015.
 */
public class EntityManager implements Tickable, Disposable, Mappable {

    private final PlayState playState;

    private final Array<Entity> entities = new Array<Entity>();
    private long lastEntity = 1;

    public EntityManager(PlayState playState) {
        this.playState = playState;
    }

    public PlayState getPlayState() {
        return playState;
    }

    @Override
    public Map getMap() {
        return playState.getMap();
    }

    public Entity spawnEntity(EntityType entityType, Vector2 location) {
        getMap().isLocationValid(location, true);

        long lastEntity = this.lastEntity + 1;

        Entity returningEntity;
        switch(entityType) {
            case PLAYER:
                returningEntity = new PlayerEntity(lastEntity, this, getMap(), location);
                break;
            case TARGET:
                returningEntity = new Target(lastEntity, this, getMap(), location);
                break;
            case BULLET:
                returningEntity = new Bullet(lastEntity, this, getMap(), location);
                break;
            default:
                return null;
        }

        //if(!(returningEntity instanceof ObjectOnGround) || location.getBlock().setObjectOnGround((ObjectOnGround) returningEntity)) {
        entities.add(returningEntity);
        this.lastEntity = lastEntity; // if is returning alive, increment the last entity id
        return returningEntity;
        /*} else {
            return null;
        }*/
    }

    @Override
    public void update(float deltaTime) {
        Array<Entity> entityArray = new Array<Entity>(entities);
        for(Entity entity : entityArray) {
            entity.update(deltaTime);
        }
        Iterator<Entity> iterator = entities.iterator();
        int size = entities.size;
        while(iterator.hasNext() && size >= 0) {
            if(!iterator.next().isAlive())
                iterator.remove();
            size--;
        }
    }

    public void draw(SpriteBatch batch, MapViewer viewer) {
        for(EntityType entityType : EntityType.drawOrderArray()) {
            for(Entity entity : getEntities(entityType)) {
                if(viewer.getOpacityForBlock(getMap().getBlockFrom(getMap().screenCoordinatesToWorldCoordinates(entity.getLocation()))) == BlockOpacity.FULLY_VISIBLE)
                    entity.draw(batch);
            }
        }
    }

    @Override
    public void dispose() {
        killEntities(true);
    }

    public Array<Entity> getEntities(EntityType... entityTypes) {
        Array<EntityType> desiredEntities = new Array<EntityType>();
        desiredEntities.addAll(entityTypes);

        Array<Entity> entities = new Array<Entity>();
        for(Entity entity : this.entities) {
            if(desiredEntities.contains(entity.getEntityType(), true))
                entities.add(entity);
        }
        return entities;
    }

    public void killEntities(boolean immediateRemoval) {
        for(EntityType type : EntityType.values()) {
            killEntities(type, immediateRemoval);
        }
    }

    public void killEntities(EntityType type, boolean immediateRemoval) {
        Iterator<Entity> iterator = entities.iterator();
        int size = entities.size;
        while(iterator.hasNext() && size >= 0) {
            Entity entity = iterator.next();
            if(entity.getEntityType() == type)
                entity.remove(immediateRemoval);
            if(!entity.isAlive())
                iterator.remove();
            size--;
        }
    }
}
