package com.jabyftw.gameclient.util.tools;

import com.badlogic.gdx.math.MathUtils;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Edited by Rafael on 25/12/2014.
 * Original source: http://stackoverflow.com/questions/6409652/random-weighted-selection-java-framework
 */
class RandomCollection<E> {

    private final NavigableMap<Float, E> navigableMap = new TreeMap<Float, E>();
    private float total;

    public void put(float weight, E value) {
        if(weight <= 0) throw new IllegalArgumentException("Weight can't be less or equal than zero.");
        total += weight;
        navigableMap.put(total, value);
    }

    public E getRandom() {
        return navigableMap.ceilingEntry(MathUtils.random() * total).getValue();
    }
}
