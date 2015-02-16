package com.jabyftw.gameclient.entity.util;

import com.jabyftw.gameclient.maps.util.Locatable;

/**
 * Created by Rafael on 13/02/2015.
 */
public interface Box2dEntity extends Entity, Locatable, Contactable {

    public void spawnBox2dBody();
}
