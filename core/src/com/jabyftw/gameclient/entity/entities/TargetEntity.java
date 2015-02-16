package com.jabyftw.gameclient.entity.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jabyftw.gameclient.entity.AbstractDamageableEntity;
import com.jabyftw.gameclient.entity.EntityManager;
import com.jabyftw.gameclient.entity.EntityType;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.maps.Converter;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.screen.Animation;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.AnimationEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;
import com.jabyftw.gameclient.util.tools.StringReplacer;

/**
 * Created by Isa on 03/01/2015.
 */
public class TargetEntity extends AbstractDamageableEntity {

    private final Animation animation;
    private DisplayText damageTakenText;
    private float damageTaken = 0;

    public TargetEntity(Long entityId, EntityManager entityManager, Map map, Vector2 spawnLocation) {
        super(entityId, entityManager, map, spawnLocation);
        animation = Resources.getAnimation(AnimationEnum.TARGET_ANIMATION);
        baseColor = Color.ORANGE;
        spawnBox2dBody();
    }

    @Override
    public void update(float deltaTime) {
        if(elapsedTimeSinceDamage > Constants.Gameplay.Target.TIME_UNTIL_DAMAGE_CLEARS)
            damageTaken = 0;

        if(damageTaken > 0) {
            if(damageTakenText != null)
                damageTakenText.dispose();

            damageTakenText = new DisplayText(
                    this,
                    Resources.getLang(LangEnum.TARGET_RECEIVED_DAMAGE),
                    new StringReplacer() {
                        @Override
                        public String replace(String before) {
                            return before.replaceAll("%damagetaken%", Util.formatDecimal(damageTaken, 1));
                        }
                    },
                    Constants.Gameplay.STEP * 2f
            );
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
        super.drawGame(batch);
    }

    @Override
    public void contactWith(Contact contact, Object objectContactedWith) {
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
            bodyDef.type = BodyDef.BodyType.KinematicBody;

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 1.2f;
            fixtureDef.restitution = 0;
            fixtureDef.friction = 0.2f;
            fixtureDef.shape = circleShape;
            fixtureDef.filter.categoryBits = Constants.Box2dConstants.BIT_ENEMY_ENTITY;
            fixtureDef.filter.maskBits = Constants.Box2dConstants.BIT_BULLET_ENTITY | Constants.Box2dConstants.BIT_PLAYER_ENTITY | Constants.Box2dConstants.BIT_ENEMY_ENTITY
                    | Constants.Box2dConstants.BIT_SOLID_BLOCK | Constants.Box2dConstants.BIT_WORLD_BOUNDS;

            createBox2dBody(map.getWorld(), bodyDef, fixtureDef);

            circleShape.dispose();
        }
    }

    @Override
    public boolean doDamage(float damage, boolean ignoreIfIsHittable) {
        if(super.doDamage(damage, ignoreIfIsHittable)) {
            damageTaken += damage;
            currentHealth += damage;
            return true;
        }
        return false;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.TARGET;
    }
}
