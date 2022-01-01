package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;

/**
 * Explicitly NOT player oriented. This is used for spawning, etc.
 *
 * @author zygon
 */
public class SetItemAction extends Action {

    private final Item identifable;
    private final Location location;

    public SetItemAction(Item item, Location location) {
        this.identifable = item;
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    @Override
    public GameState execute(GameState state) {
        World world = state.getWorld();

        world.add(identifable, location);

        return state;
    }
}
