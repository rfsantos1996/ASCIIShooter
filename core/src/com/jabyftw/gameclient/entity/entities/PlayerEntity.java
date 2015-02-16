package com.jabyftw.gameclient.entity.entities;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;

import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.AbstractDamageableEntity;
import com.jabyftw.gameclient.entity.EntityManager;
import com.jabyftw.gameclient.entity.EntityType;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.entity.util.MapViewer;
import com.jabyftw.gameclient.entity.weapon.Layout;
import com.jabyftw.gameclient.entity.weapon.WeaponsHolder;
import com.jabyftw.gameclient.entity.weapon.util.WeaponHolder;
import com.jabyftw.gameclient.maps.Block;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.maps.util.Material;
import com.jabyftw.gameclient.screen.Animation;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.AnimationEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Rafael on 29/12/2014.
 */
public class PlayerEntity extends AbstractDamageableEntity implements MapViewer {

    private final Animation animation;
    private DisplayText doInteraction = null;
    private WeaponHolder weaponHolder;
    private PointLight pointLight;

    private float timeSinceLastRun = Constants.Gameplay.Player.STAMINA_RECOVER_COOLDOWN;
    private float currentStamina = Constants.Gameplay.Player.MAXIMUM_STAMINA;
    private int selectedLayout = 0;

    public PlayerEntity(Long entityId, EntityManager entityManager, Map map, Vector2 spawnLocation) {
        super(entityId, entityManager, map, spawnLocation);
        this.animation = Resources.getAnimation(AnimationEnum.PLAYER_ANIMATION);
        setSelectedLayout(Main.getOfflineProfile().getLastSelectedLayout());
        doOnDeath();
    }

    @Override
    public void update(float deltaTime) {
        animation.update(deltaTime);

        {
            // Control controls
            boolean isFiring = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            boolean isAskingToReload = Gdx.input.isKeyJustPressed(Input.Keys.R);

            boolean isInteracting = Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.CENTER);
            boolean isChangingWeaponNext = Gdx.input.isKeyJustPressed(Input.Keys.E),
                    isChangingWeaponPast = Gdx.input.isKeyJustPressed(Input.Keys.Q);

            boolean isRunning = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
            boolean isForward = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP),
                    isBackward = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
            boolean isLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT),
                    isRight = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            boolean isMoving = isForward || isBackward || isLeft || isRight;

            // Do interaction
            doInteraction(isInteracting);

            // Do rotation
            doRotation();

            // Control speed
            doMovements(isRunning, isLeft, isRight, isForward, isBackward, isMoving, deltaTime);

            // Fire Weapon
            doWeaponing(deltaTime, isRunning, isAskingToReload, isMoving, isFiring, isChangingWeaponNext, isChangingWeaponPast);
        }
        super.update(deltaTime);
    }

    @Override
    public void drawGame(SpriteBatch batch) {
        Sprite frame = new Sprite(animation.getCurrentFrame());
        {
            frame.setOriginCenter();
            frame.setColor(getDamageFilterColor());
            frame.setRotation((float) Math.toDegrees(box2dBody.getAngle()) - 90);

            Vector2 position = box2dBody.getPosition().cpy().sub(Constants.Gameplay.Entities.BODY_RADIUS).scl(Constants.Display.PIXELS_PER_METER);
            frame.setPosition(position.x, position.y);
            frame.setScale(Constants.Display.BASE_TILE_SCALE);

            batch.begin();
            frame.draw(batch);
            batch.end();
        }
        weaponHolder.drawGame(batch);
        super.drawGame(batch);
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        weaponHolder.drawHUD(batch);
        super.drawHUD(batch);
    }

    @Override
    protected void doOnDeath() {
        timeSinceLastRun = Constants.Gameplay.Player.STAMINA_RECOVER_COOLDOWN;
        currentStamina = Constants.Gameplay.Player.MAXIMUM_STAMINA;
        currentHealth = Constants.Gameplay.Entities.DEFAULT_HEALTH;

        if(weaponHolder != null) weaponHolder.resetStats();
        weaponHolder = new WeaponsHolder(this, Main.getOnlineProfile().getLayouts()[selectedLayout]);

        super.doOnDeath();
        spawnBox2dBody();
    }

    private void doWeaponing(float deltaTime, boolean isRunning, boolean isAskingToReload, boolean isMoving, boolean isFiring, boolean isChangingWeaponNext, boolean isChangingWeaponPast) {
        if(isRunning)
            weaponHolder.getSelectedWeapon().setElapsedReloadTime(0);
        else if(isAskingToReload)
            weaponHolder.getSelectedWeapon().askToReload();
        else if(isFiring && getAgeTicks() >= 10)
            weaponHolder.getSelectedWeapon().fire(deltaTime, this, box2dBody.getPosition(), (float) Math.toDegrees(box2dBody.getAngle()));
        else if(isMoving)
            weaponHolder.getSelectedWeapon().setReloadTimeMultiplier(0.75f);
        else
            weaponHolder.getSelectedWeapon().setReloadTimeMultiplier(1);

        // Change weapons with E/Q
        if(isChangingWeaponNext || isChangingWeaponPast)
            weaponHolder.selectWeaponType(isChangingWeaponNext);
        weaponHolder.update(deltaTime);
    }

    private void doMovements(boolean isRunning, boolean isLeft, boolean isRight, boolean isForward, boolean isBackward, boolean isMoving, float deltaTime) {
        boolean isHorizontal = (isLeft && !isRight) || (!isLeft && isRight),
                isVertical = (isForward && !isBackward) || (!isForward && isBackward);

        if(isRunning) {
            timeSinceLastRun = 0;
            currentStamina -= deltaTime;
            if(currentStamina < 0) currentStamina = 0;
        } else {
            timeSinceLastRun += deltaTime;
            if(timeSinceLastRun >= Constants.Gameplay.Player.STAMINA_RECOVER_COOLDOWN && currentStamina < Constants.Gameplay.Player.MAXIMUM_STAMINA)
                currentStamina += Math.min((isMoving ? 0.6f : 1) * deltaTime, Constants.Gameplay.Player.MAXIMUM_STAMINA - currentStamina);
        }

        isRunning = isRunning && currentStamina > 0;
        float speedMultiplierByWorld = map.getBlockFrom(Converter.BOX2D_COORDINATES.toWorldCoordinates(box2dBody.getPosition())).getMaterial().getSpeedMultiplier();
        float playerSpeed = (Constants.Gameplay.Player.BASE_SPEED + (isRunning ? Constants.Gameplay.Player.RUNNING_SPEED : 0)) * speedMultiplierByWorld;

        {
            // Do movements
            Vector2 velocity = box2dBody.getLinearVelocity();

            float horizontalSpeed = playerSpeed * (isVertical ? MathUtils.cosDeg(45) : 1) * 1.5f;
            float verticalSpeed = playerSpeed * (isHorizontal ? MathUtils.sinDeg(45) : 1) * 1.5f;

            // Convert speed to 'Box2d speed'
            Vector2 box2dSpeed = Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(horizontalSpeed, verticalSpeed));
            horizontalSpeed = box2dSpeed.x;
            verticalSpeed = box2dSpeed.y;

            box2dBody.setLinearVelocity(
                    isHorizontal ? Math.min(velocity.x, velocity.x < 0 ? -horizontalSpeed : horizontalSpeed) : 0,
                    isVertical ? Math.min(velocity.y, velocity.y < 0 ? -verticalSpeed : verticalSpeed) : 0
            );
            velocity = box2dBody.getLinearVelocity();

            if(isHorizontal && Math.abs(velocity.x) < horizontalSpeed) {
                box2dBody.setLinearVelocity(isRight ? horizontalSpeed : -horizontalSpeed, isVertical ? velocity.y : 0);

                // Update, if changed
                velocity = box2dBody.getLinearVelocity();
            }

            if(isVertical && Math.abs(velocity.y) < verticalSpeed) {
                box2dBody.setLinearVelocity(isHorizontal ? velocity.x : 0, isForward ? verticalSpeed : -verticalSpeed);
            }
        }
    }

    private void doInteraction(boolean isInteracting) {
        if(doInteraction != null) doInteraction.dispose();

        Array<Block> blockArray = map.filterBlocksByMaterial(
                map.getBlocksNear(Converter.BOX2D_COORDINATES.toWorldCoordinates(box2dBody.getPosition()), Constants.Gameplay.Player.INTERACT_DISTANCE),
                Material.CLOSED_DOOR,
                Material.OPEN_DOOR
        );

        if(blockArray.size > 0) {
            doInteraction = new DisplayText(this, Resources.getLang(LangEnum.DO_INTERACTION));

            if(isInteracting)
                //noinspection LoopStatementThatDoesntLoop
                for(Block block : blockArray) {
                    if(map.getBlockFrom(box2dBody.getPosition()) != block) {
                        block.setInteracted(this);
                        break;
                    }
                }
        }
    }

    private void doRotation() {
        float angle = (float) Math.toDegrees(box2dBody.getAngle());
        float deltaRotation = getDeltaRotation(getMouseDeltaRotation(), angle);

        if(deltaRotation <= 0)
            angle += Math.max(-Constants.Gameplay.Player.MAX_ROTATION_SPEED, deltaRotation);
        else
            angle += Math.min(Constants.Gameplay.Player.MAX_ROTATION_SPEED, deltaRotation);

        box2dBody.setTransform(box2dBody.getPosition(), (float) Math.toRadians((angle + 360) % 360));
    }

    @Override
    public void spawnBox2dBody() {
        Vector2 bodyRadius = Constants.Gameplay.Entities.BODY_RADIUS;
        {
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(bodyRadius.x + bodyRadius.y);

            BodyDef bodyDef = new BodyDef();
            bodyDef.fixedRotation = true;
            bodyDef.position.set(Converter.WORLD_COORDINATES.toBox2dCoordinates(spawnLocation.cpy()));
            bodyDef.type = BodyDef.BodyType.DynamicBody;

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 1.2f;
            fixtureDef.restitution = 0;
            fixtureDef.friction = 0.2f;
            fixtureDef.shape = circleShape;
            fixtureDef.filter.categoryBits = Constants.Box2dConstants.BIT_PLAYER_ENTITY;
            fixtureDef.filter.maskBits = Constants.Box2dConstants.BIT_BULLET_ENTITY | Constants.Box2dConstants.BIT_PLAYER_ENTITY | Constants.Box2dConstants.BIT_SOLID_BLOCK
                    | Constants.Box2dConstants.BIT_WORLD_BOUNDS | Constants.Box2dConstants.BIT_ENEMY_ENTITY;

            createBox2dBody(map.getWorld(), bodyDef, fixtureDef);

            circleShape.dispose();
        }
        pointLight = Util.createPointLight(map.getRayHandler(), 256, Constants.Gameplay.Player.PLAYER_LIGHT_COLOR, Constants.Gameplay.Player.VIEW_DISTANCE, box2dBody);
    }

    @Override
    public void removeBox2dBody() {
        if(pointLight != null) {
            pointLight.remove();
            pointLight = null;
        }
        super.removeBox2dBody();
    }

    @Override
    public void contactWith(Contact contact, Object objectContactedWith) {
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
    public float getOpacityForBlock(Block block) {
        if(block.getBox2dBody().getPosition().dst2(box2dBody.getPosition()) <= Util.square(Constants.Gameplay.Player.VIEW_DISTANCE))
            return 1;
        return 0;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    public WeaponHolder getWeaponHolder() {
        return weaponHolder;
    }
}
