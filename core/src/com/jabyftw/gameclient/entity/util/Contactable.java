package com.jabyftw.gameclient.entity.util;

import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Created by Rafael on 14/02/2015.
 */
public interface Contactable {

    public void contactWith(Contact contact, Object objectContactedWith);

}
