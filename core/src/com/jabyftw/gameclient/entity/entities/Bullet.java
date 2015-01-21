package com.jabyftw.gameclient.entity.entities;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.AbstractBox2dEntity;
import com.jabyftw.gameclient.entity.util.Box2dConstants;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.entity.weapon.WeaponProperties;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.util.Util;

/**
 * Created by Isa on 02/01/2015.
 */
public class Bullet extends AbstractBox2dEntity {

    private static final Vector2 bodyRadius = Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(1 / 4f / 2f, 1 / 4f / 2f).scl(MathUtils.cosDeg(45), MathUtils.sinDeg(45)));
    private static final int LIGHT_QUALITY = 56;

    private PointLight pointLight;
    private Entity owner;
    private WeaponProperties weaponProperties;

    private float angle;
    private Vector2 spawnLocation;

    protected Bullet(long entityId, EntityManager entityManager, Map map, Vector2 location) {
        super(entityId, entityManager, map);
        this.spawnLocation = location;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.BULLET;
    }

    @Override
    public void update(float deltaTime) {
        float maxDistance = Util.square(weaponProperties.getType().getMaxDistance());
        if(spawnLocation.dst2(box2dBody.getPosition()) > maxDistance) {
            remove(false);
        }
        super.update(deltaTime);
    }

    /*@Override
    public void update(float deltaTime) {
        if(elapsedDistance < maximumDistance) {
            float distanceX = bulletSpeed * cos * deltaTime;
            float distanceY = bulletSpeed * sin * deltaTime;

            moveAndDamage(distanceX, distanceY);
        } else {
            remove(false);
        }
        super.update(deltaTime);
    }*/

    /*@Override
    public void draw(SpriteBatch batch) {
        ShapeRenderer shapeRenderer = Map.shapeRenderer;
        {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.line(
                    box2dBody.getPosition().x - (BULLET_LENGTH * Map.TILE_WIDTH * Map.BOX2D_TILE_SCALE_WIDTH * MathUtils.cosDeg(box2dBody.getAngle())),
                    box2dBody.getPosition().y - (BULLET_LENGTH * Map.TILE_HEIGHT * Map.BOX2D_TILE_SCALE_HEIGHT * MathUtils.sinDeg(box2dBody.getAngle())),
                    box2dBody.getPosition().x + (BULLET_LENGTH * Map.TILE_WIDTH * Map.BOX2D_TILE_SCALE_WIDTH * MathUtils.cosDeg(box2dBody.getAngle())),
                    box2dBody.getPosition().y + (BULLET_LENGTH * Map.TILE_HEIGHT * Map.BOX2D_TILE_SCALE_HEIGHT * MathUtils.sinDeg(box2dBody.getAngle())),
                    Color.ORANGE,
                    Color.RED
            );
            shapeRenderer.end();
        }
        super.draw(batch);
    }*/

    @Override
    public void spawnBox2dBody() {
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.fixedRotation = true;
            bodyDef.bullet = true;
            bodyDef.position.set(spawnLocation);
            bodyDef.angle = (float) Math.toRadians(angle);
            bodyDef.linearVelocity.set(
                    weaponProperties.getBulletSpeedMetersSecond() / Main.PIXELS_PER_METER * MathUtils.cos(bodyDef.angle),
                    weaponProperties.getBulletSpeedMetersSecond() / Main.PIXELS_PER_METER * MathUtils.sin(bodyDef.angle)
            );
            bodyDef.type = BodyDef.BodyType.DynamicBody;

            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(bodyRadius.x + bodyRadius.y);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.restitution = 0.2f;
            fixtureDef.density = 0.8f;
            fixtureDef.filter.categoryBits = Box2dConstants.BIT_BULLET;
            fixtureDef.filter.maskBits = Box2dConstants.BIT_BLOCK | Box2dConstants.BIT_ENEMY | Box2dConstants.BIT_WORLD_BOUNDS;
            fixtureDef.shape = circleShape;
            {
                createBox2dBody(map.getWorld(), bodyDef, fixtureDef);
            }
            circleShape.dispose();
        }
        Color randomColor = WeaponProperties.BASE_COLOR.cpy().add(-MathUtils.random(0.05f, 0.2f), MathUtils.random(0.05f, 0.15f), MathUtils.random(0.05f, 0.20f), 0).sub(0, 0, 0, 1 - 0.7f);
        pointLight = Util.createPointLight(map.getRayHandler(), LIGHT_QUALITY, randomColor, weaponProperties.getLightDistance(), box2dBody);
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

    /*private float getBulletDamage(float bulletDamage) {
        float ratio = 1f - (elapsedDistance * 0.5f) / weaponProperties.getType().getEffectiveDistance();
        if(ratio < 0.05f)
            ratio = 0.05f;
        else if(ratio > 1)
            ratio = 1;
        return ratio * bulletDamage;
    }

    private void moveAndDamage(float x, float y) {
        Pair<Vector2, MapLocation> pair = spawnLocation.canMove(x, y, false);

        if(!pair.getB().isValid()) {
            remove(false);
            return;
        }

        x = pair.getA().x;
        y = pair.getA().y;

        elapsedDistance += Math.abs(x) + Math.abs(y);
        spawnLocation.add(x, y);

        {
            // Do damage if damageable else remove if block
            ObjectOnGround objectOnGround = spawnLocation.getBlock().getObjectOnGround();
            if(objectOnGround != null && objectOnGround != owner && objectOnGround != this) {
                remove(false);
                if(objectOnGround instanceof Damageable && ((Damageable) objectOnGround).isHittable())
                    ((Damageable) objectOnGround).doDamage(getBulletDamage(bulletDamage));
            }
        }
    }*/

    public void setProperties(Entity owner, float angle, WeaponProperties weaponProperties) {
        this.weaponProperties = weaponProperties;
        this.owner = owner;
        this.angle = angle;
        spawnBox2dBody();
    }

    public Entity getOwner() {
        return owner;
    }

    @Override
    public Vector2 getLocation() {
        return box2dBody.getPosition();
    }
}
