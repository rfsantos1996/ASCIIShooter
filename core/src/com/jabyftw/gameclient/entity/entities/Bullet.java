package com.jabyftw.gameclient.entity.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.jabyftw.gameclient.entity.AbstractEntity;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.maps.Map;

/**
 * Created by Isa on 02/01/2015.
 */
public class Bullet extends AbstractEntity {

    public static final float DEFAULT_BULLET_SPEED = 17.5f;
    public static final float BULLET_LENGTH = (0.25f / 2f);
    public static final float DEFAULT_EFFECTIVE_DISTANCE = 14;
    public static final float DEFAULT_MAXIMUM_BULLET_DISTANCE = 16;
    public static final float DEFAULT_BULLET_DAMAGE = 1.34f;

    private Entity owner;

    private float elapsedDistance = 0;
    private float bulletSpeed = DEFAULT_BULLET_SPEED;
    private float bulletDamage = DEFAULT_BULLET_DAMAGE;
    private float effectiveDistance = DEFAULT_EFFECTIVE_DISTANCE;
    private float maximumDistance = DEFAULT_MAXIMUM_BULLET_DISTANCE;

    private float cos, sin;

    private Body box2dBody;

    protected Bullet(long entityId, EntityManager entityManager, Map map, Vector2 location) {
        super(entityId, entityManager, map);
        cos = (float) Math.cos(Math.toRadians(location.angle()));
        sin = (float) Math.sin(Math.toRadians(location.angle()));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.BULLET;
    }

    @Override
    public void update(float deltaTime) {
        /*if(elapsedDistance < maximumDistance) {
            float distanceX = bulletSpeed * cos * deltaTime;
            float distanceY = bulletSpeed * sin * deltaTime;

            moveAndDamage(distanceX, distanceY);
        } else {
            remove(false);
        }*/
        super.update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch) {
        ShapeRenderer shapeRenderer = Map.shapeRenderer;
        {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.line(
                    box2dBody.getPosition().x - (BULLET_LENGTH * Map.TILE_WIDTH * Map.BOX2D_TILE_SCALE_WIDTH * cos),
                    box2dBody.getPosition().y - (BULLET_LENGTH * Map.TILE_HEIGHT * Map.BOX2D_TILE_SCALE_HEIGHT * sin),
                    box2dBody.getPosition().x + (BULLET_LENGTH * Map.TILE_WIDTH * Map.BOX2D_TILE_SCALE_WIDTH * cos),
                    box2dBody.getPosition().y + (BULLET_LENGTH * Map.TILE_HEIGHT * Map.BOX2D_TILE_SCALE_HEIGHT * sin),
                    Color.ORANGE,
                    Color.RED
            );
            shapeRenderer.end();
        }
        super.draw(batch);
    }

    private float getBulletDamage(float bulletDamage) {
        float ratio = 1f - (elapsedDistance * 0.5f) / effectiveDistance;
        if(ratio < 0.05f)
            ratio = 0.05f;
        else if(ratio > 1)
            ratio = 1;
        return ratio * bulletDamage;
    }

    /*private void moveAndDamage(float x, float y) {
        Pair<Vector2, MapLocation> pair = location.canMove(x, y, false);

        if(!pair.getB().isValid()) {
            remove(false);
            return;
        }

        x = pair.getA().x;
        y = pair.getA().y;

        elapsedDistance += Math.abs(x) + Math.abs(y);
        location.add(x, y);

        {
            // Do damage if damageable else remove if block
            ObjectOnGround objectOnGround = location.getBlock().getObjectOnGround();
            if(objectOnGround != null && objectOnGround != owner && objectOnGround != this) {
                remove(false);
                if(objectOnGround instanceof Damageable && ((Damageable) objectOnGround).isHittable())
                    ((Damageable) objectOnGround).doDamage(getBulletDamage(bulletDamage));
            }
        }
    }*/

    public void setProperties(Entity owner, float bulletSpeed, float bulletDamage, float effectiveDistance, float maximumDistance) {
        setOwner(owner);
        setBulletSpeed(bulletSpeed);
        setBulletDamage(bulletDamage);
        setEffectiveDistance(effectiveDistance);
        setMaximumDistance(maximumDistance);
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity entity) {
        this.owner = entity;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(float bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public float getBulletDamage() {
        return bulletDamage;
    }

    public void setBulletDamage(float bulletDamage) {
        this.bulletDamage = bulletDamage;
    }

    public float getEffectiveDistance() {
        return effectiveDistance;
    }

    public void setEffectiveDistance(float effectiveDistance) {
        this.effectiveDistance = effectiveDistance;
    }

    public float getMaximumDistance() {
        return maximumDistance;
    }

    public void setMaximumDistance(float maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    @Override
    public Vector2 getLocation() {
        return box2dBody.getPosition();
    }
}
