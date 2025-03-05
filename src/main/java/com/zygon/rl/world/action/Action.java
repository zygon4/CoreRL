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

    public static Action combine(Action... actions) {
        return new Action() {
            @Override
            public boolean canExecute(GameState state) {
                return actions[0].canExecute(state);
            }

            @Override
            public GameState execute(GameState state) {
                for (Action a : actions) {
                    if (a.canExecute(state)) {
                        state = a.execute(state);
                    }
                }
                return state;
            }
        };
    }
}
