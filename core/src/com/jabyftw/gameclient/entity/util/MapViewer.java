package com.jabyftw.gameclient.entity.util;

import com.jabyftw.gameclient.maps.Block;
import com.jabyftw.gameclient.maps.util.BlockOpacity;
import com.jabyftw.gameclient.maps.util.Locatable;

/**
 * Created by Rafael on 29/12/2014.
 */
public interface MapViewer extends Locatable {

    public BlockOpacity getOpacityForBlock(Block block);

}
