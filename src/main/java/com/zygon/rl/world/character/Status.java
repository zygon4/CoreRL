package com.zygon.rl.world.character;

import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.IntegerAttribute;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author zygon
 */
public class Status {

    private final int age;
    private final int hitPoints;
    private final Set<String> effects;

    public Status(int age, int hitPoints, Set<String> effects) {
        this.age = age;
        this.hitPoints = hitPoints;
        this.effects = effects;
    }

    public int getAge() {
        return age;
    }

    public Set<String> getEffects() {
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

    public Set<Attribute> getAttributes() {
        Set<Attribute> stats = new LinkedHashSet<>();

        stats.add(IntegerAttribute.create("Age", "Age", getAge()));
        stats.add(IntegerAttribute.create("HP", "Hit Points", getHitPoints()));
        // TODO: status effects

        return stats;
    }

    // status effects tbd
}
