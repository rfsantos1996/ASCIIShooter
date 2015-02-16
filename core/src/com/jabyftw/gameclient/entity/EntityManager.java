package com.jabyftw.gameclient.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.entity.util.MapViewer;
import com.jabyftw.gameclient.gamestates.play.playstate.PlayState;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.maps.Map;
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

    @Override
    public Map getMap() {
        return playState.getMap();
    }

    public Entity spawnEntity(EntityType entityType, Vector2 worldLocation) {
        worldLocation = worldLocation.cpy();
        getMap().isLocationValid(worldLocation);

        long lastEntity = this.lastEntity + 1;
        Entity entity;

        try {
            entity = entityType.getClazz().getDeclaredConstructor(Long.class, this.getClass(), getMap().getClass(), worldLocation.getClass()).newInstance(lastEntity, this, getMap(), worldLocation);
        } catch(Throwable e) {
            e.printStackTrace();
            return null;
        }

        entities.add(entity);
        this.lastEntity = lastEntity; // if is returning alive, increment the last entity id
        return entity;
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

    public void drawGame(SpriteBatch batch, MapViewer viewer) {
        for(EntityType entityType : EntityType.drawOrderArray()) {
            for(Entity entity : getEntities(entityType)) {
                if(viewer.getOpacityForBlock(getMap().getBlockFrom(Converter.BOX2D_COORDINATES.toWorldCoordinates(entity.getLocation()))) > 0)
                    entity.drawGame(batch);
            }
        }
    }

    public void drawHUD(SpriteBatch batch, MapViewer viewer) {
        for(EntityType entityType : EntityType.drawOrderArray()) {
            for(Entity entity : getEntities(entityType)) {
                if(viewer.getOpacityForBlock(getMap().getBlockFrom(Converter.BOX2D_COORDINATES.toWorldCoordinates(entity.getLocation()))) > 0)
                    entity.drawHUD(batch);
            }
        }
    }

    @Override
    public void dispose() {
        killEntities(true);
    }

    Array<Entity> getEntities(EntityType... entityTypes) {
        Array<EntityType> desiredEntities = new Array<EntityType>();
        desiredEntities.addAll(entityTypes);

        Array<Entity> entities = new Array<Entity>();
        for(Entity entity : this.entities) {
            if(desiredEntities.contains(entity.getEntityType(), true))
                entities.add(entity);
        }
        return entities;
    }

    void killEntities(boolean immediateRemoval) {
        for(EntityType type : EntityType.values()) {
            killEntities(type, immediateRemoval);
        }
    }

    void killEntities(EntityType type, boolean immediateRemoval) {
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
