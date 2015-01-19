package com.jabyftw.gameclient.util;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Rafael on 25/12/2014.
 * Original source: http://stackoverflow.com/questions/6409652/random-weighted-selection-java-framework
 */
public class RandomCollection<E> {

    private final NavigableMap<Double, E> navigableMap = new TreeMap<Double, E>();
    private final Random random;
    private double total;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public void put(double weight, E value) {
        if(weight <= 0) throw new IllegalArgumentException("Weight can't be less or equal than zero.");
        total += weight;
        navigableMap.put(total, value);
    }

    public E getRandom() {
        return navigableMap.ceilingEntry(random.nextDouble() * total).getValue();
    }
}
