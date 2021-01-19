package com.zygon.rl.data;

import java.util.Set;

/**
 *
 */
public class Creature extends Element {

    private String species;
    private int aggression;
    private int hitPoints;
    private int speed;
    private Set<String> flags;

    public Creature() {
    }

    public Creature(String id, String type, String symbol, String color, String name, String description) {
        super(id, type, symbol, color, name, description);
    }

    public String getSpecies() {
        return species;
    }

    public int getAggression() {
        return aggression;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getSpeed() {
        return speed;
    }

    public Set<String> getFlags() {
        return flags;
    }
}
