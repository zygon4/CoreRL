package com.zygon.rl.game;

import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.Action;

import java.util.Set;
import java.util.function.Function;

/**
 * Handles directions, could be used for spells/other actions in the future.
 * There's no specific target, just a direction.
 *
 * @author zygon
 */
public class ActionDirectionInputHandler extends DirectionInputHandler {

    private final Function<Location, Action> getActionFn;

    public ActionDirectionInputHandler(GameConfiguration gameConfiguration,
            Function<Location, Action> action, Location centralLocation) {
        super(gameConfiguration, centralLocation, Set.of());

        this.getActionFn = action;
    }

    @Override
    public GameState apply(GameState state, Input input) {

        GameState newState = state;
        Location target = getTarget(input);

        if (target != null) {
            Action action = getActionFn.apply(target);
            if (action.canExecute(state)) {
                newState = action.execute(state);
            }
        } else {
            invalidInput(input);
        }

        return popInputContext(newState);
    }
}
