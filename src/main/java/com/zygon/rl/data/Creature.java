package com.zygon.rl.data;

/**
 *
 */
public class Creature extends Element {

    private String species;
    private int aggression;
    private int hitPoints;
    private int speed;

    public Creature() {
    }

    public Creature(Element template, String species, int aggression, int hitPoints, int speed) {
        super(template);
        this.species = species;
        this.aggression = aggression;
        this.hitPoints = hitPoints;
        this.speed = speed;
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
}
