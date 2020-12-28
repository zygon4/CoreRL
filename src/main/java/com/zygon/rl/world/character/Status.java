package com.zygon.rl.world.character;

import java.util.Set;

/**
 *
 * @author zygon
 */
public class Status {

    private final int hitPoints;
    private final Set<String> effects;

    public Status(int hitPoints, Set<String> effects) {
        this.hitPoints = hitPoints;
        this.effects = effects;
    }

    public Set<String> getEffects() {
        return effects;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public Status decHitPoints(int hps) {
        return new Status(hitPoints - hps, effects);
    }

    public Status incHitPoints(int hps) {
        return new Status(hitPoints + hps, effects);
    }

    // status effects tbd
}
