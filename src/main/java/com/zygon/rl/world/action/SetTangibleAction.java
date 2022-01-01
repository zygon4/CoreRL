package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Tangible;

/**
 * So for a spell.. set the fields which are not static items, not characters,
 * but a different stateful effect. They can have origins, destinations,
 * patterns, etc. The action is to place the field in a tile or propagate the
 * field to other locations.
 *
 *
 * @author zygon
 */
public class SetTangibleAction extends Action {

    private final Location location;
    private final Tangible identifable;

    // This is intended to be a actor-only summon, but summoning random items
    // is pretty valid as well. This will need enhancement.
    public SetTangibleAction(Location location, Tangible tangible) {
        this.location = location;
        this.identifable = tangible;
    }

    @Override
    public boolean canExecute(GameState state) {
        // TODO: check for things like impassable terrain
        return true;
    }

    @Override
    public GameState execute(GameState state) {
        state.getWorld().add(identifable, location);

        return state;
    }
}
