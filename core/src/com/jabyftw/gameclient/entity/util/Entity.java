package com.jabyftw.gameclient.entity.util;

import com.jabyftw.gameclient.entity.entities.EntityManager;
import com.jabyftw.gameclient.entity.entities.EntityType;
import com.jabyftw.gameclient.maps.util.Locatable;
import com.jabyftw.gameclient.maps.util.Mappable;
import com.jabyftw.gameclient.util.Drawable;
import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Rafael on 12/12/2014.
 */
public interface Entity extends Locatable, Mappable, Tickable, Drawable {

    EntityManager getEntityManager();

    public boolean isAlive();

    public EntityType getEntityType();

    public long getEntityID();

    public void remove(boolean immediateRemoval);

    public void addDisplayTextAtHead(DisplayText displayText);

    public void removeDisplayText(DisplayText displayText);
}
