package com.zygon.rl.world.character;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zygon
 */
public class Status {

    private final int age;
    private final int hitPoints;
    private final Map<String, Integer> effects;

    public Status(int age, int hitPoints, Map<String, Integer> effects) {
        this.age = age;
        this.hitPoints = hitPoints;
        this.effects = effects;
    }

    public int getAge() {
        return age;
    }

    public Map<String, Integer> getEffects() {
        return effects;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public Status decHitPoints(int hps) {
        return new Status(age, hitPoints - hps, effects);
    }

    public Status incAge() {
        return new Status(age + 1, hitPoints, effects);
    }

    public Status incHitPoints(int hps) {
        return new Status(age, hitPoints + hps, effects);
    }

    public Status addEffect(String effect) {
        return addEffect(effect, null);
    }

    public Status addEffect(String effect, Integer value) {
        Map<String, Integer> effects = new HashMap<>(this.effects);
        effects.put(effect, value);
        return new Status(age, hitPoints, effects);
    }

    public Status removeEffect(String effect) {
        Map<String, Integer> effects = new HashMap<>(this.effects);
        effects.remove(effect);
        return new Status(age, hitPoints, effects);
    }
}
