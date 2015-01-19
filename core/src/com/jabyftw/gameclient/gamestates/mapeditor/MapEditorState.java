package com.jabyftw.gameclient.gamestates.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.gamestates.util.AbstractGameState;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.maps.util.Material;
import com.jabyftw.gameclient.screen.Animation;
import com.jabyftw.gameclient.screen.MovableCamera;

/**
 * Created by Isa on 01/01/2015.
 */
public class MapEditorState extends AbstractGameState {

    private static final float CAMERA_SPEED = 3.25f, RUNNING_SPEED = 4.5f;

    private final MovableCamera gameCamera = Main.getInstance().getGameCamera();
    //private final Viewport gameViewport = Main.getInstance().getGameViewport();

    private long tickCreated;

    private Map map;
    private Vector2 selectedLocation = null;

    private Material selectedMaterial;
    private Animation selectedMaterialAnimation;

    public MapEditorState(Map map) {
        this.map = map;
        this.map.clearWorld();
        this.map.setUseLightning(false);
    }

    @Override
    public void create() {
        final MapEditorState mapEditorState = this;
        desktopProcessor = new InputAdapter() {
            @Override
            public boolean scrolled(int amount) {
                moveThroughMaterial(amount > 0);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if(Input.Keys.ESCAPE == keycode) {
                    Main.getInstance().setCurrentGameState(new MapEditorMenu(mapEditorState));
                    return true;
                }
                return super.keyDown(keycode);
            }
        };

        tickCreated = Main.getTicksPassed();
        moveThroughMaterial(true);
    }

    @Override
    public void update(float deltaTime) {
        //map.update(deltaTime);
        selectedMaterialAnimation.update(deltaTime);
        {
            // Update camera location
            Vector2 location = new Vector2(gameCamera.position.x, gameCamera.position.y);

            float cameraSpeed = CAMERA_SPEED + (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? RUNNING_SPEED : 0);
            cameraSpeed *= deltaTime;

            if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))
                location.add(0, cameraSpeed * Map.TILE_SCALE_HEIGHT);

            else if(Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))
                location.add(0, -cameraSpeed * Map.TILE_SCALE_HEIGHT);

            if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
                location.add(-cameraSpeed * Map.TILE_SCALE_WIDTH, 0);

            else if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                location.add(cameraSpeed * Map.TILE_SCALE_WIDTH, 0);

            gameCamera.updatePosition(location, false);
        }

        if(Main.getTicksPassed() - tickCreated >= (0.75f / Main.STEP)) {
            // Update mouse clicks
            boolean leftPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT),
                    rightPressed = Gdx.input.isButtonPressed(Input.Buttons.RIGHT),
                    middlePressed = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE);

            Vector3 unprojection = gameCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            Vector2 unprojectionLocation = map.screenCoordinatesToWorldCoordinates(new Vector2(unprojection.x, unprojection.y));

            if(map.isLocationValid(unprojectionLocation, true)) {
                selectedLocation = unprojectionLocation;
                if(leftPressed || rightPressed) {
                    map.getBlockFrom(unprojectionLocation).setMaterial(rightPressed ? Material.AIR : selectedMaterial);
                } else if(middlePressed) {
                    setSelectedMaterial(map.getBlockFrom(unprojectionLocation).getMaterial());
                }
            }
        }
        {
            // Update controls
            boolean pressedE = Gdx.input.isKeyJustPressed(Input.Keys.E),
                    pressedQ = Gdx.input.isKeyJustPressed(Input.Keys.Q);

            if(pressedE || pressedQ)
                moveThroughMaterial(pressedE);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.setProjectionMatrix(Main.getInstance().getGameCamera().combined);
        {
            // Draw game
            map.draw(batch);
            if(selectedLocation != null && map.isLocationValid(selectedLocation, true)) {
                // Draw background (not to show the block)
                ShapeRenderer shapeRenderer = Map.shapeRenderer;
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setProjectionMatrix(gameCamera.combined);
                shapeRenderer.setColor(Color.BLACK.sub(0, 0, 0, 1 - 0.5f));
                shapeRenderer.rect(
                        (float) (Math.floor(selectedLocation.x) * Map.TILE_SCALE_WIDTH),
                        (float) (Math.floor(selectedLocation.y) * Map.TILE_SCALE_HEIGHT),
                        0,
                        0,
                        Map.TILE_WIDTH,
                        Map.TILE_HEIGHT,
                        Map.BASE_TILE_SCALE,
                        Map.BASE_TILE_SCALE,
                        0
                );
                shapeRenderer.end();
                // Draw selected block (with opacity)
                batch.begin();
                batch.setColor(Color.ORANGE.cpy().sub(0, 0, 0, 1 - 0.6f));
                batch.draw(
                        selectedMaterialAnimation.getCurrentFrame(),
                        (float) (Math.floor(selectedLocation.x) * Map.TILE_SCALE_WIDTH),
                        (float) (Math.floor(selectedLocation.y) * Map.TILE_SCALE_HEIGHT),
                        0,
                        0,
                        Map.TILE_WIDTH,
                        Map.TILE_HEIGHT,
                        Map.BASE_TILE_SCALE,
                        Map.BASE_TILE_SCALE,
                        0
                );
                batch.end();
            }
        }
    }

    @Override
    public void dispose() {
        if(map.shouldDispose())
            map.dispose();
    }

    private void moveThroughMaterial(boolean next) {
        if(selectedMaterial == null)
            setSelectedMaterial(Material.getDefaultMaterial());

        int ordinal = selectedMaterial.ordinal();
        Material material;
        while((material = Material.valueOf(ordinal)) == selectedMaterial || !material.appearOnMapEditor()) {
            ordinal += (next ? 1 : -1);

            if(ordinal >= Material.values().length)
                ordinal = 0;
            else if(ordinal < 0)
                ordinal = Material.values().length - 1;
        }

        setSelectedMaterial(material);
    }

    private void setSelectedMaterial(Material material) {
        this.selectedMaterial = material;
        this.selectedMaterialAnimation = material.getAnimation();
    }

    public Map getMap() {
        return map;
    }

    @Override
    public Color getBackgroundColor() {
        return Color.BLACK;
    }

    @Override
    public boolean shouldRegisterInput() {
        return true;
    }
}
