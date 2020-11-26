package com.zygon.rl.game;

import com.zygon.rl.game.GameState;
import com.zygon.rl.game.Input;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 *
 * @author zygon
 */
public class InputHandler implements BiFunction<GameState, Input, GameState> {

    private static final System.Logger logger = System.getLogger(InputHandler.class.getCanonicalName());
    private static final Map<Integer, KeyCode> keyCodesByInt = new HashMap<>();

    static {
        for (KeyCode kc : KeyCode.values()) {
            keyCodesByInt.put(kc.getCode(), kc);
        }
    }

    // TBD: template pattern with a parent class or not
    @Override
    public final GameState apply(GameState state, Input input) {
//        Set<Input> validInputs = state.getInputContext().peek().getValidInputs();

//        if (validInputs.contains(input)) {
        return doApply(state, input);
//        } else {
//            invalidInput(input);
//            return state;
//        }
    }

    // This bleeds out the zircon API. This basically says this is a zircon
    // "core" jar.
    protected final KeyCode convert(Input input) {
        return keyCodesByInt.get(input.getInput());
    }

    // Override this!
    protected GameState doApply(GameState state, Input input) {
        return state;
    }

    protected final void invalidInput(Input input) {
        // TODO: logg
        logger.log(Level.ALL, input);
    }
}
