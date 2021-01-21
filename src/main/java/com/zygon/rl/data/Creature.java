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

    public Creature(Element template, String species, int aggression, int hitPoints, int speed, Set<String> flags) {
        super(template);
        this.species = species;
        this.aggression = aggression;
        this.hitPoints = hitPoints;
        this.speed = speed;
        this.flags = flags;
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
