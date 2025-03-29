package com.zygon.rl.world.character;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.zygon.rl.data.Effect;
import com.zygon.rl.data.PoolData;
import com.zygon.rl.world.Attribute;

/**
 * TODO: hit points are bespoke here because they're so ingrained. Could be
 * removed from explicit mention.
 *
 * @author zygon
 */
public final class Status {

    private static final int MIN_ENERGY = 100;

    private final int age;
    private final Map<String, Pool> poolsById;
    private final String healthPoolId;
    // effects ability to move/act, this is a private implementation detail
    private final int energy;
    private final Map<String, StatusEffect> effects;

    private Status(int age,
            Map<String, Pool> poolsById,
            int energy,
            Map<String, StatusEffect> effects) {
        this.age = age;
        this.poolsById = poolsById;
        // This is a re-calc that happens every set, could be optimized
        this.healthPoolId = poolsById.values().stream()
                .filter(p -> p.getPoolData().actsAsHealth())
                .map(Pool::getPoolData)
                .map(PoolData::getId)
                .findAny().orElseThrow(() -> new RuntimeException("bad pool "
                + poolsById.keySet().stream().collect(Collectors.joining(","))));
        this.energy = energy;
        this.effects = effects;
    }

    public Status(int age,
            Set<Pool> pools,
            Set<StatusEffect> effects) {
        this(age,
                Collections.unmodifiableMap(pools.stream()
                        .collect(Collectors.toMap(k -> k.getPoolData().getId(), v -> v))),
                0,
                Collections.unmodifiableMap(effects.stream()
                        .collect(Collectors.toMap(k -> k.getEffect().getId(), v -> v))));
    }

    // Used to calculate ability to move/act, not a traditional stat or "mana", etc.
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
        return this.poolsById.get(this.healthPoolId).getPoints();
    }

    public int getMaxHitPoints() {
        return this.poolsById.get(this.healthPoolId).getMax();
    }

    public Status decHitPoints(int hps) {
        Map<String, Pool> poolsByName = new HashMap<>(this.poolsById);
        poolsByName.put(this.healthPoolId,
                poolsByName.get(this.healthPoolId).decrement(hps));
        return new Status(age, poolsByName, energy, effects);
    }

    public Status incAge() {
        return new Status(age + 1, poolsById, energy, effects);
    }

    public Status incHitPoints(int hps) {
        Map<String, Pool> poolsById = new HashMap<>(this.poolsById);
        poolsById.put(this.healthPoolId,
                poolsById.get(this.healthPoolId).increment(hps));
        return new Status(age, poolsById, energy, effects);
    }

    public Status addEffect(StatusEffect effect) {
        Map<String, StatusEffect> effects = new HashMap<>(this.effects);
        effects.put(effect.getEffect().getId(), effect);

        return new Status(age, poolsById, energy, effects);
    }

    public Status removeEffect(String effect) {
        Map<String, StatusEffect> effects = new HashMap<>(this.effects);
        effects.remove(effect);
        return new Status(age, poolsById, energy, effects);
    }

    /*pkg*/ Status incEnergy(int amount) {
        return new Status(age, poolsById, energy + amount, effects);
    }

    /*pkg*/ Status resetEnergy() {
        return new Status(age, poolsById, energy - MIN_ENERGY, effects);
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

    public Pool getPool(String poolName) {
        return poolsById.get(poolName);
    }

    public Set<String> getPoolNames() {
        return Collections.unmodifiableSet(poolsById.keySet());
    }

    public Status setPool(Pool pool) {
        Map<String, Pool> poolsByName = new HashMap<>(this.poolsById);
        poolsByName.put(pool.getName(), pool);
        return new Status(age, poolsByName, energy, effects);
    }
}
