package com.jabyftw.gameclient.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.maps.Map;

/**
 * Created by Rafael on 12/12/2014.
 */
public abstract class AbstractEntity implements Entity {

    private final Array<DisplayText> displayTextArray = new Array<DisplayText>();
    private final EntityManager entityManager;
    private final long entityId, tickCreated;

    private boolean doRemove = false;
    private boolean alive = true;
    private long tickOfDeath = -1;

    protected final Map map;
    protected final Vector2 spawnLocation;
    protected Color baseColor = Color.WHITE;


    public AbstractEntity(long entityId, EntityManager entityManager, Map map, Vector2 spawnLocation) {
        this.entityId = entityId;
        this.entityManager = entityManager;
        this.map = map;
        this.spawnLocation = spawnLocation;
        this.tickCreated = Main.getTicksPassed();
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
    public long getAgeTicks() {
        return Main.getTicksPassed() - (tickOfDeath > 0 ? tickOfDeath : tickCreated);
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
     * @param deltaTime time in seconds since last drawGame
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
    public void drawGame(SpriteBatch batch) {
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        batch.setProjectionMatrix(Main.getInstance().getGameCamera().combined);
        { // Even being drawn on game camera, it is supposed to draw after the game
            for(int i = 0; i < displayTextArray.size; i++) {
                displayTextArray.get(i).draw(batch, i);
            }
        }
        batch.setProjectionMatrix(Main.getInstance().getHudCamera().combined);
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
        this.alive = false;
        this.tickOfDeath = Main.getTicksPassed();
    }

    @Override
    public Map getMap() {
        return map;
    }
}
