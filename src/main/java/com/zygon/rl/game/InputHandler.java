package com.zygon.rl.game;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This is the primary entry point for the game. It should include composed
 * handlers.
 *
 * @author zygon
 */
public final class InputHandler implements BiFunction<GameState, Input, GameState> {

    // Would contain all of the state transition functions
    private final Map<String, LayerInputHandler> stateFnByContextName;

    public InputHandler(Map<String, LayerInputHandler> stateFnByContextName) {
        this.stateFnByContextName = stateFnByContextName != null
                ? Collections.unmodifiableMap(stateFnByContextName) : Collections.emptyMap();
    }

    @Override
    public final GameState apply(GameState state, Input input) {

        String contextLayerName = state.getInputContext().peek().getName();
        BiFunction<GameState, Input, GameState> stateFn = stateFnByContextName
                .get(contextLayerName);

        if (stateFn != null) {
            return stateFn.apply(state, input);
        }

        throw new IllegalStateException("Unknown layer name: " + contextLayerName);
    }
}
