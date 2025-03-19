package com.zygon.rl.world.character;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.zygon.rl.data.Effect;
import com.zygon.rl.world.Attribute;

/**
 *
 * @author zygon
 */
public final class Status {

    private static final int MIN_ENERGY = 100;

    private final int age;
    private final int hitPoints;
    // effects ability to move/act, this is a private implementation detail
    private final int energy;
    private final Map<String, StatusEffect> effects;

    private Status(int age, int hitPoints, int energy,
            Map<String, StatusEffect> effects) {
        this.age = age;
        this.hitPoints = hitPoints;
        this.energy = energy;
        this.effects = effects;
    }

    public Status(int age, int hitPoints, Set<StatusEffect> effects) {
        this(age, hitPoints, 0, Collections.unmodifiableMap(effects.stream()
                .collect(Collectors.toMap(k -> k.getEffect().getId(), v -> v))));
    }

    public boolean canAct() {
        return energy >= MIN_ENERGY;
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
        return new Status(age, hitPoints - hps, energy, effects);
    }

    public Status incAge() {
        return new Status(age + 1, hitPoints, energy, effects);
    }

    public Status incHitPoints(int hps) {
        return new Status(age, hitPoints + hps, energy, effects);
    }

    public Status addEffect(StatusEffect effect) {
        Map<String, StatusEffect> effects = new HashMap<>(this.effects);
        effects.put(effect.getEffect().getId(), effect);

        return new Status(age, hitPoints, energy, effects);
    }

    public Status removeEffect(String effect) {
        Map<String, StatusEffect> effects = new HashMap<>(this.effects);
        effects.remove(effect);
        return new Status(age, hitPoints, energy, effects);
    }


    // Used to calculate ability to move/act, not a traditional stat or "mana", etc.
    /*pkg*/ int getEnergy() {
        return energy;
    }

    /*pkg*/ Status incEnergy(int amount) {
        return new Status(age, hitPoints, energy + amount, effects);
    }

    /*pkg*/ Status resetEnergy() {
        return new Status(age, hitPoints, energy - MIN_ENERGY, effects);
    }

    public Set<Attribute> getEffectAttributes() {
        Set<Attribute> effectAttrs = new HashSet<>();

        for (String key : effects.keySet()) {
            Effect effect = effects.get(key).getEffect();
            effectAttrs.add(Attribute.builder()
                    .setName(effect.getName())
                    .setDescription(effect.getDescription())
                    .setValue("")
                    .build());
        }

        return effectAttrs;
    }
}
