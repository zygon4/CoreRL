package com.zygon.rl.world.action;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;

/**
 * Explicitly NOT player oriented. This is used for spawning, etc.
 *
 * @author zygon
 */
public class RemoveItemAction extends Action {

    private final Identifable identifable;
    private final Location location;

    public RemoveItemAction(Identifable item, Location location) {
        this.identifable = item;
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {
        Identifable ident = state.getWorld().getAll(location, null).stream()
                .filter(id -> id.getId().equals(identifable.getId()))
                .findAny().orElse(null);

        return ident != null;
    }

    @Override
    public GameState execute(GameState state) {
        World world = state.getWorld();

        world.remove(identifable, location);

        return state;
    }
}
