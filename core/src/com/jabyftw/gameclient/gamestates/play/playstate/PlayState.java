package com.jabyftw.gameclient.gamestates.play.playstate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.entities.EntityManager;
import com.jabyftw.gameclient.entity.entities.EntityType;
import com.jabyftw.gameclient.entity.entities.PlayerEntity;
import com.jabyftw.gameclient.gamestates.util.AbstractGameState;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.screen.MovableCamera;

/**
 * Created by Rafael on 08/12/2014.
 */
public class PlayState extends AbstractGameState {

    private Map map;

    private EntityManager entityManager;
    private PlayerEntity playerEntity;

    private boolean editableMap = false;

    public PlayState(Map map, boolean editableMap) {
        this.map = map;
        this.editableMap = editableMap;
        this.map.setUseLightning(true);
    }

    @Override
    public void create() {
        entityManager = new EntityManager(this);
        map.setViewer((playerEntity = (PlayerEntity) entityManager.spawnEntity(EntityType.PLAYER, map.getSpawnLocation())));

        {
            final PlayState playState = this;
            desktopProcessor = new InputAdapter() {
                @Override
                public boolean scrolled(int amount) {
                    playerEntity.getWeaponHolder().selectWeaponType(amount > 0);
                    return true;
                }

                @Override
                public boolean keyDown(int keycode) {
                    if(Input.Keys.ESCAPE == keycode) {
                        Main.getInstance().setCurrentGameState(new PlayStateMenu(playState, editableMap));
                        return true;
                    }
                    return super.keyDown(keycode);
                }
            };
        }
    }

    @Override
    public void update(float deltaTime) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            entityManager.spawnEntity(EntityType.TARGET, Converter.BOX2D_COORDINATES.toWorldCoordinates(playerEntity.getLocation().cpy()));
        }
        {
            // Update cameras' position
            MovableCamera gameCamera = Main.getInstance().getGameCamera();
            gameCamera.updatePosition((map.getViewer().getLocation() != null ? Converter.BOX2D_COORDINATES.toScreenCoordinates(map.getViewer().getLocation()) : new Vector2(map.getWidth() / 2, map.getHeight() / 2)), true);
        }
        entityManager.update(deltaTime);
        map.update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setProjectionMatrix(Main.getInstance().getGameCamera().combined);
        {
            map.draw(batch);
            entityManager.draw(batch, map.getViewer());
        }
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }

    @Override
    public void dispose() {
        entityManager.dispose();
        if(map.shouldDispose())
            map.dispose();
    }

    @Override
    public Color getBackgroundColor() {
        return Color.BLACK;
    }

    public Map getMap() {
        return map;
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }
}
