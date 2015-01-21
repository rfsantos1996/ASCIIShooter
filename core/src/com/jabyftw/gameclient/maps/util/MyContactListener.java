package com.jabyftw.gameclient.maps.util;

import com.badlogic.gdx.physics.box2d.*;
import com.jabyftw.gameclient.entity.AbstractBox2dEntity;
import com.jabyftw.gameclient.entity.util.Box2dConstants;

/**
 * Created by Rafael on 21/01/2015.
 */
public class MyContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture worldBoundsCollision = checkContactFor(contact, Box2dConstants.BIT_WORLD_BOUNDS);
        if(worldBoundsCollision != null && worldBoundsCollision.getFilterData().categoryBits != Box2dConstants.BIT_PLAYER) {
            AbstractBox2dEntity userData = (AbstractBox2dEntity) worldBoundsCollision.getUserData();
            userData.remove(false);
        }

        Fixture blockCollision = checkContactFor(contact, Box2dConstants.BIT_BLOCK);
        if(blockCollision != null && blockCollision.getFilterData().categoryBits == Box2dConstants.BIT_BULLET) {
            AbstractBox2dEntity userData = (AbstractBox2dEntity) blockCollision.getUserData();
            userData.remove(false);
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    /**
     * Check the collision for something colliding to a specific category
     *
     * @param contact needed for checking
     * @param categoryBits the wanted category
     * @return the collided fixture, null if both were the same categoryBits
     */
    private Fixture checkContactFor(Contact contact, short categoryBits) {
        boolean fixtureAisTheWall = contact.getFixtureA().getFilterData().categoryBits == categoryBits,
                fixtureBisTheWall = contact.getFixtureB().getFilterData().categoryBits == categoryBits;

        if((fixtureAisTheWall && !fixtureBisTheWall) || (!fixtureAisTheWall && fixtureBisTheWall))
            return (fixtureAisTheWall ? contact.getFixtureB() : contact.getFixtureA());
        else
            return null;
    }
}
