package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;

/**
 * Can be anything from move, to use ability, to simulate physics, to attack.
 * TODO: pass in state, get back state.
 *
 * @author zygon
 */
public abstract class Action {

    public abstract boolean canExecute(GameState state);

    public abstract GameState execute(GameState state);
}
