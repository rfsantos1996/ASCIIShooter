package com.jabyftw.gameclient.entity.entities;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.AbstractDamageableEntity;
import com.jabyftw.gameclient.entity.util.Box2dConstants;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.entity.util.MapViewer;
import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.entity.weapon.WeaponHolder;
import com.jabyftw.gameclient.entity.weapon.WeaponsHolder;
import com.jabyftw.gameclient.maps.Block;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.maps.util.BlockOpacity;
import com.jabyftw.gameclient.maps.util.Material;
import com.jabyftw.gameclient.screen.Animation;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.AnimationEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 29/12/2014.
 */
public class PlayerEntity extends AbstractDamageableEntity implements MapViewer {

    private static final Vector2 bodyRadius = new Vector2(((Map.BOX2D_TILE_SCALE_WIDTH / 4f) * MathUtils.cosDeg(45)), ((Map.BOX2D_TILE_SCALE_HEIGHT / 4f) * MathUtils.sinDeg(45)));

    // Other stuff
    private final Animation animation;

    // Properties
    public static final float DEFAULT_HEALTH = 10f;
    private static final float INTERACT_DISTANCE = 1.5f;
    private static final float VIEW_DISTANCE = 10;
    private static final float BASE_SPEED = 1.8f, RUNNING_SPEED = 0.9f;
    private static final float STAMINA_DECREASE = 1f, MAXIMUM_STAMINA = STAMINA_DECREASE * 4f, STAMINA_RECOVER_COOLDOWN = 3.5f;
    private static final float MAX_ROTATION_SPEED = 360 * Main.STEP;

    // Weapons and layouts
    private WeaponHolder weaponHolder;
    private int selectedLayout = 0;

    // DisplayText
    private DisplayText doInteraction = null;

    // Variables
    private float lastRun = STAMINA_RECOVER_COOLDOWN;
    private float stamina = MAXIMUM_STAMINA;
    private long tickCreated;

    private Vector2 spawnLocation;
    private PointLight pointLight;

    public PlayerEntity(long entityId, EntityManager entityManager, Map map, Vector2 location) {
        super(entityId, entityManager, map, DEFAULT_HEALTH);
        this.animation = Resources.getAnimation(AnimationEnum.PLAYER_ANIMATION);
        this.tickCreated = Main.getTicksPassed();
        this.setSelectedLayout(0);
        this.spawnLocation = location;
        doOnDeath();
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);

        {
            // Do rotation
            doRotation();

            // Control controls
            boolean isFiring = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            boolean isAskingToReload = Gdx.input.isKeyJustPressed(Input.Keys.R);

            boolean isInteracting = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.CENTER);
            boolean isChangingWeaponNext = Gdx.input.isKeyJustPressed(Input.Keys.E),
                    isChangingWeaponPast = Gdx.input.isKeyJustPressed(Input.Keys.Q);

            boolean isRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && stamina > deltaTime;
            boolean isForward = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP),
                    isBackward = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean isLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT),
                    isRight = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            boolean isMoving = isForward || isBackward || isLeft || isRight;

            // Do interaction
            doInteraction(isInteracting);

            // Control speed
            float playerSpeed = (BASE_SPEED + (isRunning ? RUNNING_SPEED : 0)) * map.getBlockFrom(map.screenCoordinatesToWorldCoordinates(box2dBody.getPosition())).getMaterial().getSpeedMultiplier();
            doMovements(isRunning, isLeft, isRight, isForward, isBackward, isMoving, deltaTime, playerSpeed);

            // Fire Weapon
            doWeaponing(isRunning, isAskingToReload, isMoving, isFiring, isChangingWeaponNext, isChangingWeaponPast, deltaTime);
            weaponHolder.update(deltaTime);
        }
        super.update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch) {
        Sprite frame = new Sprite(animation.getCurrentFrame());
        {
            frame.setOriginCenter();
            frame.setRotation((float) Math.toDegrees(box2dBody.getAngle()) - 90);

            Vector2 position = box2dBody.getPosition().cpy().sub(bodyRadius).scl(Main.PIXELS_PER_METER);
            frame.setPosition(position.x, position.y);
            frame.setScale(Map.BASE_TILE_SCALE);

            batch.begin();
            frame.draw(batch);
            batch.end();
        }
        weaponHolder.draw(batch);
        super.draw(batch);
    }

    @Override
    protected void doOnDeath() {
        lastRun = STAMINA_RECOVER_COOLDOWN;
        stamina = MAXIMUM_STAMINA;
        health = MAXIMUM_HEALTH;
        if(weaponHolder != null) weaponHolder.resetStats();
        weaponHolder = new WeaponsHolder(this, Main.getOnlineProfile().getLayouts()[selectedLayout]);

        super.doOnDeath();
        spawnBox2dBody();
    }

    private void doWeaponing(boolean isRunning, boolean isAskingToReload, boolean isMoving, boolean isFiring, boolean isChangingWeaponNext, boolean isChangingWeaponPast, float deltaTime) {
        if(isRunning)
            weaponHolder.getSelectedWeapon().setElapsedReloadTime(0);
        else if(isAskingToReload)
            weaponHolder.getSelectedWeapon().askToReload();
        else if(isFiring && (Main.getTicksPassed() - tickCreated) >= 10)
            weaponHolder.getSelectedWeapon().fire(deltaTime, this, box2dBody.getPosition(), (float) Math.toDegrees(box2dBody.getAngle()));
        else if(isMoving)
            weaponHolder.getSelectedWeapon().setReloadTimeMultiplier(0.75f);
        else
            weaponHolder.getSelectedWeapon().setReloadTimeMultiplier(1);

        // Change weapons with E/Q
        if(isChangingWeaponNext || isChangingWeaponPast)
            weaponHolder.selectWeaponType(isChangingWeaponNext);
    }

    private void doMovements(boolean isRunning, boolean isLeft, boolean isRight, boolean isForward, boolean isBackward, boolean isMoving, float deltaTime, float playerSpeed) {
        boolean isHorizontal = isLeft || isRight,
                isVertical = isForward || isBackward;

        if(isRunning) {
            lastRun = 0;
            stamina -= STAMINA_DECREASE * deltaTime;
        } else {
            lastRun += deltaTime;
            if(lastRun >= STAMINA_RECOVER_COOLDOWN && stamina < MAXIMUM_STAMINA)
                stamina += Math.min((isMoving ? STAMINA_DECREASE / 2f : STAMINA_DECREASE) * deltaTime, MAXIMUM_STAMINA - stamina);
        }

        {
            // Do movements
            //Vector2 position = box2dBody.getPosition();
            Vector2 velocity = box2dBody.getLinearVelocity();

            box2dBody.setLinearVelocity(isHorizontal ? velocity.x : 0, isVertical ? velocity.y : 0);
            velocity = box2dBody.getLinearVelocity();

            float horizontalSpeed = playerSpeed * Map.BOX2D_TILE_SCALE_WIDTH * (isVertical ? MathUtils.cosDeg(45) : 1) * 1.5f;
            float verticalSpeed = playerSpeed * Map.BOX2D_TILE_SCALE_HEIGHT * (isHorizontal ? MathUtils.sinDeg(45) : 1) * 1.5f;

            if(isHorizontal && Math.abs(velocity.x) < horizontalSpeed) {
                if(isLeft && !isRight)
                    box2dBody.setLinearVelocity(-horizontalSpeed, isVertical ? velocity.y : 0);
                    //box2dBody.applyLinearImpulse(-horizontalSpeed, 0, position.x, position.y, true);
                    //else if(isRight && !isLeft)
                else if(!isLeft)
                    box2dBody.setLinearVelocity(horizontalSpeed, isVertical ? velocity.y : 0);
                //box2dBody.applyLinearImpulse(horizontalSpeed, 0, position.x, position.y, true);

                // Update, if changed
                velocity = box2dBody.getLinearVelocity();
            }

            if(isVertical && Math.abs(velocity.y) < verticalSpeed) {
                if(isForward && !isBackward)
                    box2dBody.setLinearVelocity(isHorizontal ? velocity.x : 0, verticalSpeed);
                    //box2dBody.applyLinearImpulse(0, verticalSpeed, position.x, position.y, true);
                    //else if(isBackward && !isForward)
                else if(!isForward)
                    box2dBody.setLinearVelocity(isHorizontal ? velocity.x : 0, -verticalSpeed);
                //box2dBody.applyLinearImpulse(0, -verticalSpeed, position.x, position.y, true);
            }
        }
    }

    private void doInteraction(boolean isInteracting) {
        Array<Block> blockArray = map.filterBlocksByMaterial(map.getBlocksNear(box2dBody.getPosition(), INTERACT_DISTANCE), Material.CLOSED_DOOR, Material.OPEN_DOOR);
        if(blockArray.size > 0) {
            if(doInteraction != null) doInteraction.dispose();
            doInteraction = new DisplayText(this, Resources.getLang(LangEnum.DO_INTERACTION));
            if(isInteracting)
                //noinspection LoopStatementThatDoesntLoop
                for(Block block : blockArray) {
                    if(map.getBlockFrom(box2dBody.getPosition()) != block)
                        block.setInteracted();
                    break;
                }
        }
    }

    private void doRotation() {
        float angle = (float) Math.toDegrees(box2dBody.getAngle());
        float deltaRotation = getDeltaRotation(getMouseDeltaRotation(), angle);

        if(deltaRotation <= 0)
            angle += Math.max(-MAX_ROTATION_SPEED, deltaRotation);
        else
            angle += Math.min(MAX_ROTATION_SPEED, deltaRotation);

        box2dBody.setTransform(box2dBody.getPosition(), (float) Math.toRadians((angle + 360) % 360));
    }

    @Override
    public void spawnBox2dBody() {
        {
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(bodyRadius.x + bodyRadius.y);

            BodyDef bodyDef = new BodyDef();
            bodyDef.fixedRotation = true;
            bodyDef.position.set(spawnLocation);
            bodyDef.position.scl(Map.BOX2D_TILE_SCALE_WIDTH, Map.BOX2D_TILE_SCALE_HEIGHT);
            bodyDef.type = BodyDef.BodyType.DynamicBody;

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 1.2f;
            fixtureDef.restitution = 0;
            fixtureDef.friction = 0.2f;
            fixtureDef.shape = circleShape;
            fixtureDef.filter.categoryBits = Box2dConstants.BIT_PLAYER;
            fixtureDef.filter.maskBits = Box2dConstants.BIT_BULLET | Box2dConstants.BIT_PLAYER | Box2dConstants.BIT_BLOCK | Box2dConstants.BIT_WORLD_BOUNDS;

            createBox2dBody(map.getWorld(), bodyDef, fixtureDef);

            circleShape.dispose();
        }
        pointLight = Util.createPointLight(map.getRayHandler(), 256, new Color(1, 1, 1, 0.75f), VIEW_DISTANCE, box2dBody);
    }

    @Override
    public void removeBox2dBody() {
        if(pointLight != null) {
            pointLight.dispose();
            pointLight.remove();
            pointLight = null;
        }
        super.removeBox2dBody();
    }

    public void setSelectedLayout(int selectedLayout) {
        this.selectedLayout = selectedLayout;

        Layout[] layouts = Main.getOnlineProfile().getLayouts();
        if(this.selectedLayout >= layouts.length)
            this.selectedLayout = 0;
        else if(this.selectedLayout < 0)
            this.selectedLayout = layouts.length - 1;
    }

    public int getSelectedLayout() {
        return selectedLayout;
    }

    /*private void move(float x, float y) {
        location.entityMove(this, x, y);
        box2dBody.getPosition().set(location.toBox2dVector(false));
    }

    private void move(MapLocation location) {
        if(location.isValid() && location.getBlock().setObjectOnGround(this)) {
            this.location.getBlock().setObjectOnGround(null);
            this.location.set(location);
        }
    }*/

    private float getMouseDeltaRotation() {
        Vector3 unprojectionFromMouse = map.getBox2dCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        float atan2 = (float) Math.toDegrees(MathUtils.atan2(
                unprojectionFromMouse.y - box2dBody.getPosition().y,
                unprojectionFromMouse.x - box2dBody.getPosition().x
        ));

        return (360 + atan2) % 360;
    }

    private float getDeltaRotation(float targetRotation, float actualRotation) {
        float[] difference = {
                (targetRotation - actualRotation) % 360,
                ((targetRotation - actualRotation) % 360) - 360,
                (((targetRotation - actualRotation) % 360) + 360)
        };

        float[] lowest = {360, 360};

        for(float value : difference) {
            float lowestValue = Math.min(Math.abs(value), lowest[0]);
            if(lowestValue <= lowest[0]) {
                lowest[0] = lowestValue;
                lowest[1] = Math.abs(value) == lowestValue ? value : lowest[1];
            }
        }

        return lowest[1];
    }

    @Override
    public Vector2 getLocation() {
        return box2dBody.getPosition();
    }

    @Override
    public BlockOpacity getOpacityForBlock(Block block) {
        if(block.getBox2dBody().getPosition().dst2(box2dBody.getPosition()) <= Util.square(VIEW_DISTANCE))
            return BlockOpacity.FULLY_VISIBLE;
        return BlockOpacity.UNDISCOVERED;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    public WeaponHolder getWeaponHolder() {
        return weaponHolder;
    }
}
