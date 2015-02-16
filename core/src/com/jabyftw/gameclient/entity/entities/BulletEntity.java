package com.jabyftw.gameclient.entity.entities;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jabyftw.gameclient.entity.AbstractBox2dEntity;
import com.jabyftw.gameclient.entity.EntityManager;
import com.jabyftw.gameclient.entity.EntityType;
import com.jabyftw.gameclient.entity.util.DamageableEntity;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.entity.weapon.util.WeaponProperties;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;

/**
 * Created by Isa on 02/01/2015.
 */
public class BulletEntity extends AbstractBox2dEntity {

    private static final Vector2 bodyRadius = Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(1 / 4f / 2f, 1 / 4f / 2f).scl(MathUtils.cosDeg(45), MathUtils.sinDeg(45)));

    private WeaponProperties weaponProperties;
    private PointLight pointLight;
    private Entity owner;
    private float angle;

    public BulletEntity(Long entityId, EntityManager entityManager, Map map, Vector2 spawnLocation) {
        super(entityId, entityManager, map, spawnLocation);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.BULLET;
    }

    public void setProperties(Entity owner, float angle, WeaponProperties weaponProperties) {
        this.weaponProperties = weaponProperties;
        this.owner = owner;
        this.angle = angle;
        spawnBox2dBody();
    }

    @Override
    public void update(float deltaTime) {
        float maxDistance = Util.square(weaponProperties.getWeaponType().getMaxDistance());

        if(spawnLocation.dst2(box2dBody.getPosition()) > maxDistance)
            remove(false);

        super.update(deltaTime);
    }

    @Override
    public void drawGame(SpriteBatch batch) {
    }

    @Override
    public void spawnBox2dBody() {
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.fixedRotation = true;
            bodyDef.bullet = true;
            bodyDef.position.set(spawnLocation);
            bodyDef.angle = (float) Math.toRadians(angle);
            bodyDef.linearVelocity.set(
                    weaponProperties.getBulletSpeedMetersSecond() / Constants.Display.PIXELS_PER_METER * MathUtils.cos(bodyDef.angle),
                    weaponProperties.getBulletSpeedMetersSecond() / Constants.Display.PIXELS_PER_METER * MathUtils.sin(bodyDef.angle)
            );
            bodyDef.type = BodyDef.BodyType.DynamicBody;

            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(bodyRadius.x + bodyRadius.y);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.restitution = 0.2f;
            fixtureDef.density = 0.8f;
            fixtureDef.filter.categoryBits = Constants.Box2dConstants.BIT_BULLET_ENTITY;
            fixtureDef.filter.maskBits = Constants.Box2dConstants.BIT_SOLID_BLOCK | Constants.Box2dConstants.BIT_ENEMY_ENTITY | Constants.Box2dConstants.BIT_WORLD_BOUNDS;
            fixtureDef.shape = circleShape;
            {
                createBox2dBody(map.getWorld(), bodyDef, fixtureDef);
            }
            circleShape.dispose();
        }
        Color randomColor = Constants.Gameplay.Bullet.BULLET_COLOR.cpy().add(-MathUtils.random(0.05f, 0.2f), MathUtils.random(0.05f, 0.15f), MathUtils.random(0.05f, 0.20f), 1).add(0, 0, 0, 8);
        pointLight = Util.createPointLight(map.getRayHandler(), Constants.Gameplay.Bullet.BULLET_LIGHT_QUALITY, randomColor, weaponProperties.getLightDistance(), box2dBody);
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

    @Override
    public void contactWith(Contact contact, Object objectContactedWith) {
        if(objectContactedWith instanceof DamageableEntity && !objectContactedWith.equals(owner)) {
            ((DamageableEntity) objectContactedWith).doDamage(getBulletDamage(), false);
            remove(false);
        }
    }

    private float getBulletDamage() {
        float ratio = 1 - (spawnLocation.dst2(box2dBody.getPosition()) * 0.5f) / Util.square(weaponProperties.getWeaponType().getEffectiveDistance());

        if(ratio < 0.05f)
            ratio = 0.05f;
        else if(ratio > 1)
            ratio = 1;

        return ratio * weaponProperties.getBulletDamage();
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
    }

    @Override
    public void drawGame(SpriteBatch batch) {
        ShapeRenderer shapeRenderer = Map.shapeRenderer;
        {
            Vector2 positionCoordinates = Converter.WORLD_COORDINATES.toScreenCoordinates(Converter.BOX2D_COORDINATES.toWorldCoordinates(box2dBody.getPosition()).sub(1, 0)),
                    radiusCoordinates = Converter.WORLD_COORDINATES.toScreenCoordinates(new Vector2(1 / 6f, 1 / 6f));

            Vector2 display = new Vector2(positionCoordinates).sub(MathUtils.PI / 2f, 0);

            System.out.println("Display: " + display.toString() + " position: " + positionCoordinates.toString() + " distance: " + positionCoordinates.dst(display));

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(new Color(1, 0.2f, 0.7f, 1));
            shapeRenderer.ellipse(
                    display.x,
                    display.y,
                    radiusCoordinates.x,
                    radiusCoordinates.y
            );
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.point(positionCoordinates.x, positionCoordinates.y, 0);
            shapeRenderer.end();
        }
        super.drawGame(batch);
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

    public Entity getOwner() {
        return owner;
    }

    @Override
    public Vector2 getLocation() {
        return box2dBody.getPosition();
    }
}
