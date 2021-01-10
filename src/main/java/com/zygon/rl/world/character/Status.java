package com.zygon.rl.world.character;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zygon
 */
public final class Status {

    private final int age;
    private final int hitPoints;
    private final Map<String, StatusEffect> effects;

    public Status(int age, int hitPoints, Map<String, StatusEffect> effects) {
        this.age = age;
        this.hitPoints = hitPoints;
        this.effects = Collections.unmodifiableMap(effects);
    }

    public int getAge() {
        return age;
    }

    public Map<String, StatusEffect> getEffects() {
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

    public Status addEffect(StatusEffect effect) {
        Map<String, StatusEffect> effects = new HashMap<>(this.effects);
        effects.put(effect.getId(), effect);

        return new Status(age, hitPoints, effects);
    }

    public Status removeEffect(String effect) {
        Map<String, StatusEffect> effects = new HashMap<>(this.effects);
        effects.remove(effect);
        return new Status(age, hitPoints, effects);
    }
}
