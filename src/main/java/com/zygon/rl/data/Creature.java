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

    public Creature() {
    }

    public Creature(WorldElement template, String species, int aggression,
            List<String> pools, int speed) {
        super(template);
        this.species = species;
        this.aggression = aggression;
        this.pools = pools;
        this.speed = speed;
    }

    public Creature copy(WorldElement template) {
        return new Creature(template, species, aggression, pools, speed);
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

    public void setAggression(int aggression) {
        this.aggression = aggression;
    }

    public void setPools(List<String> pools) {
        this.pools = pools;
    }

    public void setProficiencies(Map<String, Integer> proficiencies) {
        this.proficiencies = proficiencies;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
