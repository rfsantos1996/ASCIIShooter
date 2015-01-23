package com.jabyftw.gameclient.maps;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.jabyftw.gameclient.entity.util.Box2dConstants;
import com.jabyftw.gameclient.entity.util.Entity;
import com.jabyftw.gameclient.entity.util.MapViewer;
import com.jabyftw.gameclient.maps.util.BlockOpacity;
import com.jabyftw.gameclient.maps.util.Mappable;
import com.jabyftw.gameclient.maps.util.Material;
import com.jabyftw.gameclient.screen.Animation;
import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Rafael on 15/01/2015.
 */
public class Block implements Tickable, Mappable {

    private Map map;
    private Material material;
    private Animation materialAnimation;

    private Body box2dBody;

    protected static Block createBlock(BodyDef bodyDef, FixtureDef fixtureDef, Map map, int x, int y, Material material) {
        return new Block(bodyDef, fixtureDef, map, x, y, material);
    }

    private Block(BodyDef bodyDef, FixtureDef fixtureDef, Map map, int x, int y, Material material) {
        this.map = map;
        World world = map.getWorld();

        bodyDef.position.set(Converter.WORLD_COORDINATES.toBox2dCoordinates(new Vector2(x, y)));
        box2dBody = world.createBody(bodyDef);
        box2dBody.createFixture(fixtureDef);

        setMaterial(material);
    }

    @Override
    public void update(float deltaTime) {
        materialAnimation.update(deltaTime);
    }

    public void draw(SpriteBatch batch, MapViewer viewer) {
        BlockOpacity blockOpacity = viewer.getOpacityForBlock(this);
        if(blockOpacity.getOpacity() <= 0) return;

        {
            batch.setColor(new Color(1, 1, 1, blockOpacity.getOpacity()));

            Vector2 drawPosition = Converter.BOX2D_COORDINATES.toScreenCoordinates(box2dBody.getPosition());
            batch.draw(
                    materialAnimation.getCurrentFrame(),
                    drawPosition.x,
                    drawPosition.y,
                    0,
                    0,
                    Map.TILE_WIDTH,
                    Map.TILE_HEIGHT,
                    Map.BASE_TILE_SCALE,
                    Map.BASE_TILE_SCALE,
                    0
            );
        }
    }

    @Override
    public Map getMap() {
        return map;
    }

    public Material getMaterial() {
        return material;
    }

    public Body getBox2dBody() {
        return box2dBody;
    }

    public void setMaterial(Material material) {
        this.material = material;
        this.materialAnimation = material.getAnimation();
        for(Fixture fixture : box2dBody.getFixtureList()) {
            {
                Filter fixtureFilter = fixture.getFilterData();
                fixtureFilter.categoryBits = material.isSolid() ? Box2dConstants.BIT_BLOCK : Box2dConstants.BIT_EMPTY_BLOCK;
                fixture.setFilterData(fixtureFilter);
            }
        }
    }

    public void setInteracted(Entity interactingEntity) {
        // Interact if entity isn't inside the block
        if(!map.getBlockFrom(Converter.BOX2D_COORDINATES.toWorldCoordinates(interactingEntity.getLocation())).equals(this)) {
            if(material == Material.CLOSED_DOOR)
                setMaterial(Material.OPEN_DOOR);

            else if(material == Material.OPEN_DOOR)
                setMaterial(Material.CLOSED_DOOR);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Block)) return false;
        Vector2 otherBlock = Converter.BOX2D_COORDINATES.toWorldCoordinates(((Block) obj).getBox2dBody().getPosition());
        Vector2 thisBlock = Converter.BOX2D_COORDINATES.toWorldCoordinates(box2dBody.getPosition());
        return MathUtils.floorPositive(otherBlock.x) == MathUtils.floorPositive(thisBlock.x) && MathUtils.floorPositive(otherBlock.y) == MathUtils.floorPositive(thisBlock.y);
    }
}
