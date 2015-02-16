package com.jabyftw.gameclient.maps.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.jabyftw.gameclient.entity.util.Contactable;
import com.jabyftw.gameclient.util.Constants;
import com.jabyftw.gameclient.util.Util;

/**
 * Created by Rafael on 21/01/2015.
 */
public class MyContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        if(Constants.isDebugging)
            System.out.println("A: " + contact.getFixtureA().getUserData().getClass().getName() + " B: " + contact.getFixtureB().getUserData().getClass().getName());

        Object[] userDataArray = new Object[]{
                contact.getFixtureA().getUserData(),
                contact.getFixtureB().getUserData()
        };

        for(int i = 0; i < userDataArray.length; i++) {
            Object userData = userDataArray[i];

            if(userData instanceof Contactable)
                ((Contactable) userData).contactWith(contact, userDataArray[Util.fixIndex(i + 1, userDataArray.length)]);
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
}
