/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.character;

import com.zygon.rl.data.character.Proficiencies;

/**
 *
 * @author djc
 */
public class Proficiency {

    private final Proficiencies proficiency;
    private final int points;

    public Proficiency(Proficiencies proficiency, int points) {
        this.proficiency = proficiency;
        this.points = points;
    }

    public Proficiencies getProficiency() {
        return proficiency;
    }

    public int getPoints() {
        return points;
    }

    public Proficiency setPoints(int points) {
        return new Proficiency(proficiency, points);
    }

    public Proficiency incPoints() {
        return new Proficiency(proficiency, points + 1);
    }
}
