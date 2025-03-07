package com.zygon.rl.data;

import java.util.List;

/**
 *
 */
public class Creature extends WorldElement {

    private String species;
    private int aggression;
    private int hitPoints;
    private int speed;
    private List<String> spawns;

    public Creature() {
    }

    public Creature(WorldElement template, String species, int aggression,
            int hitPoints, int speed, List<String> spawns) {
        super(template);
        this.species = species;
        this.aggression = aggression;
        this.hitPoints = hitPoints;
        this.speed = speed;
        this.spawns = spawns;
    }

    public Creature(WorldElement template, String species, int aggression,
            int hitPoints, int speed) {
        this(template, species, aggression, hitPoints, speed, null);
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

    public List<String> getSpawns() {
        return spawns;
    }

    public void setAggression(int aggression) {
        this.aggression = aggression;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public void setSpawns(List<String> spawns) {
        this.spawns = spawns;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
