package com.jabyftw.gameclient.entity.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.entity.AbstractDamageableEntity;
import com.jabyftw.gameclient.entity.util.DisplayText;
import com.jabyftw.gameclient.maps.Map;
import com.jabyftw.gameclient.screen.Animation;
import com.jabyftw.gameclient.util.StringReplacer;
import com.jabyftw.gameclient.util.Util;
import com.jabyftw.gameclient.util.files.Resources;
import com.jabyftw.gameclient.util.files.enums.AnimationEnum;
import com.jabyftw.gameclient.util.files.enums.LangEnum;

/**
 * Created by Isa on 03/01/2015.
 */
public class Target extends AbstractDamageableEntity {

    // Statics
    public static final float MAXIMUM_HEALTH = 10f;
    public static final float TIME_UNTIL_DAMAGE_CLEARS = 2.5f;

    // Animation
    private Animation animation;
    private Body box2dBody;

    // Variables
    private DisplayText damageTakenText;
    private float damageTaken = 0;

    protected Target(long entityId, EntityManager entityManager, Map map, Vector2 location) {
        super(entityId, entityManager, map, MAXIMUM_HEALTH);
        animation = Resources.getAnimation(AnimationEnum.TARGET_ANIMATION);
        baseColor = Color.ORANGE;
    }

    @Override
    public void update(float deltaTime) {
        if(elapsedTimeSinceDamage > TIME_UNTIL_DAMAGE_CLEARS)
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
                    Main.STEP * 2f
            );
        }
        super.update(deltaTime);
    }

    @Override
    public void draw(SpriteBatch batch) {
        Sprite frame = new Sprite(animation.getCurrentFrame());
        {
            // Prepare the image
            frame.setColor(getDamageFilterColor());
            frame.setOriginCenter();
            frame.setScale(Map.BOX2D_TILE_SCALE_WIDTH / Main.PIXELS_PER_METER, Map.BOX2D_TILE_SCALE_HEIGHT / Main.PIXELS_PER_METER);
            frame.setPosition(box2dBody.getPosition().x, box2dBody.getPosition().y);
        }
        batch.begin();
        frame.draw(batch);
        batch.end();
        super.draw(batch);
    }

    @Override
    public void doDamage(float damage) {
        damageTaken += damage;
        super.doDamage(damage);
    }

    @Override
    protected void doOnDeath() {
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.TARGET;
    }

    @Override
    public Vector2 getLocation() {
        return box2dBody.getPosition();
    }
}
