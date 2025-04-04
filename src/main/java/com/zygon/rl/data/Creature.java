package com.zygon.rl.data;

import java.util.List;
import java.util.Map;

/**
 * Needs stats?
 */
public class Creature extends WorldElement {

    private String species;
    private int aggression;
    private List<String> pools;
    private Map<String, Integer> proficiencies;
    private int speed;
    private List<String> spawns;

    public Creature() {
    }

    public Creature(WorldElement template, String species, int aggression,
            List<String> pools, int speed, List<String> spawns) {
        super(template);
        this.species = species;
        this.aggression = aggression;
        this.pools = pools;
        this.speed = speed;
        this.spawns = spawns;
    }

    public Creature(WorldElement template, String species, int aggression,
            List<String> pools, int speed) {
        this(template, species, aggression, pools, speed, null);
    }

    public Creature copy(WorldElement template) {
        return new Creature(template, species, aggression, pools, speed, spawns);
    }

    public String getSpecies() {
        return species;
    }

    public int getAggression() {
        return aggression;
    }

    public List<String> getPools() {
        return pools;
    }

    public Map<String, Integer> getProficiencies() {
        return proficiencies;
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

    public void setPools(List<String> pools) {
        this.pools = pools;
    }

    public void setProficiencies(Map<String, Integer> proficiencies) {
        this.proficiencies = proficiencies;
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
