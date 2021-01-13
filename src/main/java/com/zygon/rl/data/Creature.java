package com.zygon.rl.data;

import java.util.Set;

/**
 *
 */
public class Creature extends Element {

    private String species;
    private int aggression;
    private int hitPoints;
    private Set<String> flags;

    public String getSpecies() {
        return species;
    }

    public int getAggression() {
        return aggression;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public Set<String> getFlags() {
        return flags;
    }
}
