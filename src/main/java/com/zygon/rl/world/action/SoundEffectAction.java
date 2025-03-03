/*
 * Copyright Liminal Data Systems 2024
 */
package com.zygon.rl.world.action;

import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;

/**
 *
 * @author djc
 */
public class SoundEffectAction extends Action {

    private final GameConfiguration gameConfiguration;
    private final String effectId;

    public SoundEffectAction(GameConfiguration gameConfiguration,
            String effectId) {
        this.gameConfiguration = gameConfiguration;
        this.effectId = effectId;
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    @Override
    public GameState execute(GameState state) {
        this.gameConfiguration.getSoundEffects().play(this.effectId);
        return state;
    }
}
