package com.zygon.rl.game;

import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.Item;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Will need to think about Listing NPCs..
 *
 * @author zygon
 */
public class ListItemInputHandler extends BaseInputHandler {

    private final Function<Item, Action> getActionFn;
    private final Map<Input, Item> inputs;

    private ListItemInputHandler(GameConfiguration gameConfiguration,
            Map<Input, Item> inputs, Function<Item, Action> getActionFn) {
        super(gameConfiguration, inputs.keySet());

        this.inputs = inputs;
        this.getActionFn = getActionFn;
    }

    public static final ListItemInputHandler create(GameConfiguration gameConfiguration,
            List<Item> inputItems, Function<Item, Action> getActionFn) {
        Map<Input, Item> inputs = createAlphaInputs(inputItems);
        return new ListItemInputHandler(gameConfiguration, inputs, getActionFn);
    }

    @Override
    public GameState apply(GameState state, Input input) {
        Item item = inputs.get(input);
        Action elementAction = getActionFn.apply(item);

        GameState newState = state;

        if (elementAction.canExecute(state)) {
            newState = elementAction.execute(state);
        }

        return newState.copy().removeInputContext().build();
    }

    @Override
    public String getDisplayText(Input input) {
        Item item = inputs.get(input);
        return item.getName();
    }
}
