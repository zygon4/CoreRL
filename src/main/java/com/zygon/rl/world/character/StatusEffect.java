package com.zygon.rl.world.character;

import com.zygon.rl.data.Effect;
import com.zygon.rl.game.GameState;

/**
 * Maybe the runtime version of Effect for player status?
 *
 * @author zygon
 */
public final class StatusEffect {

    private final Effect effectId;
    private final int turn;

    public StatusEffect(Effect id, int turn) {
        this.effectId = id;
        this.turn = turn;
    }

    public Effect getEffect() {
        return effectId;
    }

    public int getTurn() {
        return turn;
    }

    public static StatusEffect create(Effect id, GameState state) {
        return new StatusEffect(id, state.getTurnCount());
    }
}
