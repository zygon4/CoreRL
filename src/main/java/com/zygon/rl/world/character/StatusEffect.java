package com.zygon.rl.world.character;

/**
 * Maybe the runtime version of Effect for player status?
 *
 * @author zygon
 */
public final class StatusEffect {

    private final String effectId;

    public StatusEffect(String id) {
        this.effectId = id;
    }

    public String getId() {
        return effectId;
    }
}
