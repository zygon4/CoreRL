package com.zygon.rl.world.character;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public final class Status {

    private final int age;
    private final int hitPoints;
    private final Map<String, StatusEffect> effects;

    private Status(int age, int hitPoints, Map<String, StatusEffect> effects) {
        this.age = age;
        this.hitPoints = hitPoints;
        this.effects = effects;
    }

    public Status(int age, int hitPoints, Set<StatusEffect> effects) {
        this(age, hitPoints, Collections.unmodifiableMap(effects.stream()
                .collect(Collectors.toMap(k -> k.getId(), v -> v))));
    }

    public int getAge() {
        return age;
    }

    public boolean isEffected(String effectId) {
        return getEffects().get(effectId) != null;
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
