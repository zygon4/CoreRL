package com.zygon.rl.world.character;

import com.zygon.rl.data.Effect;

/**
 * Maybe the runtime version of Effect for player status?
 *
 * @author zygon
 */
public final class StatusEffect {

    private final Effect effectId;

    public StatusEffect(Effect id) {
        this.effectId = id;
    }

    public Effect getEffect() {
        return effectId;
    }
}
