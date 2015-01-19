package com.jabyftw.gameclient.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.entity.entities.EntityManager;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.maps.Map;

/**
 * Created by Rafael on 12/12/2014.
 */
public abstract class AbstractEntity implements Entity {

    protected final Array<DisplayText> displayTextArray = new Array<DisplayText>();
    protected final long entityId;

    protected EntityManager entityManager;
    protected Map map;
    protected boolean doRemove = false, alive = true;

    protected Color baseColor = Color.WHITE;

    protected AbstractEntity(long entityId, EntityManager entityManager, Map map) {
        this.entityId = entityId;
        this.entityManager = entityManager;
        this.map = map;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public long getEntityID() {
        return entityId;
    }

    @Override
    public void remove(boolean immediateRemoval) {
        if(immediateRemoval)
            doRemoveEntity();
        else
            doRemove = true;
    }

    /**
     * This method will run the doRemoveEntity(), so **remember casting super.update()!**
     *
     * @param deltaTime time in seconds since last draw
     */
    @Override
    public void update(float deltaTime) {
        if(doRemove)
            doRemoveEntity();
        for(DisplayText displayText : displayTextArray) {
            displayText.update(deltaTime);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        for(int i = 0; i < displayTextArray.size; i++) {
            displayTextArray.get(i).draw(batch, i);
        }
    }

    @Override
    public void addDisplayTextAtHead(DisplayText displayText) {
        displayTextArray.add(displayText);
    }

    @Override
    public void removeDisplayText(DisplayText displayText) {
        displayTextArray.removeValue(displayText, true);
    }

    protected void doRemoveEntity() {
        alive = false;
    }

    @Override
    public Map getMap() {
        return map;
    }
}
