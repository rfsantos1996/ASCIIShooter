package com.jabyftw.gameclient.entity.util;

import com.jabyftw.gameclient.entity.EntityManager;
import com.jabyftw.gameclient.entity.EntityType;
import com.jabyftw.gameclient.maps.util.Locatable;
import com.jabyftw.gameclient.maps.util.Mappable;
import com.jabyftw.gameclient.util.GameDrawable;
import com.jabyftw.gameclient.util.HudDrawable;
import com.jabyftw.gameclient.util.Tickable;

/**
 * Created by Rafael on 12/12/2014.
 */
public interface Entity extends Locatable, Mappable, Tickable, GameDrawable, HudDrawable {

    public EntityManager getEntityManager();

    public EntityType getEntityType();

    public boolean isAlive();

    public long getAgeTicks();

    public long getEntityID();

    public void remove(boolean immediateRemoval);

    public void addDisplayTextAtHead(DisplayText displayText);

    public void removeDisplayText(DisplayText displayText);
}
