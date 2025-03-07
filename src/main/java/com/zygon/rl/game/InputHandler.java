package com.zygon.rl.game;

import java.lang.System.Logger.Level;
import java.util.function.BiFunction;

/**
 * This is the primary entry point for inputs. It peeks at the top of the
 * context stack and calls the handler.
 *
 * Does this need to exist? Could this functionality live in Game?
 *
 * @author zygon
 */
public final class InputHandler implements BiFunction<GameState, Input, GameState> {

    private static final System.Logger logger = System.getLogger(
            InputHandler.class.getCanonicalName());

    @Override
    public final GameState apply(GameState state, Input input) {

        GameState.InputContext currentContext = state.getInputContext().peek();
        LayerInputHandler handler = currentContext.getHandler();

        if (handler.getInputs().contains(input)) {
            return handler.apply(state, input);
        } else {
            String errMessage = "Invalid input";

            logger.log(Level.DEBUG, errMessage);
            return handler.handleInvalidInput(state.log(errMessage));
        }
    }
}
