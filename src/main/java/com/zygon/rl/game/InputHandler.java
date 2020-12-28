package com.zygon.rl.game;

import java.util.function.BiFunction;

/**
 * This is the primary entry point for inputs.
 *
 * @author zygon
 */
public final class InputHandler implements BiFunction<GameState, Input, GameState> {

    private static final System.Logger logger = System.getLogger(InputHandler.class.getCanonicalName());

    @Override
    public final GameState apply(GameState state, Input input) {

        GameState.InputContext currentContext = state.getInputContext().peek();
        LayerInputHandler handler = currentContext.getHandler();

        if (handler.getInputs().contains(input)) {
            return handler.apply(state, input);
        } else {
            logger.log(System.Logger.Level.INFO,
                    "Invalid input for " + state.getInputContext().peek().getName());
            return state.copy().removeInputContext().build();
        }
    }
}
