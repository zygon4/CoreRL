package com.zygon.rl.game;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.world.action.Action;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Will need to think about Listing NPCs..
 *
 * @author zygon
 * @param <T>
 */
public class ListActionInputHandler<T extends Identifable> extends BaseInputHandler {

    private final Function<T, Action> getActionFn;
    private final Map<Input, T> inputs;

    private ListActionInputHandler(GameConfiguration gameConfiguration,
            Map<Input, T> inputs, Function<T, Action> getActionFn) {
        super(gameConfiguration, inputs.keySet());

        this.inputs = inputs;
        this.getActionFn = getActionFn;
    }

    public static final <T extends Identifable> ListActionInputHandler<T> create(
            GameConfiguration gameConfiguration, List<T> inputItems, Function<T, Action> getActionFn) {
        Map<Input, T> inputs = createAlphaInputs(inputItems);
        return new ListActionInputHandler<>(gameConfiguration, inputs, getActionFn);
    }

    @Override
    public GameState apply(GameState state, Input input) {
        T t = inputs.get(input);
        Action elementAction = getActionFn.apply(t);

        GameState newState = state;

        if (elementAction.canExecute(state)) {
            newState = elementAction.execute(state);
        }

        return newState.copy().removeInputContext().build();
    }

    @Override
    public String getDisplayText(Input input) {
        T t = inputs.get(input);
        return Data.get(t.getId()).getName();
    }
}
