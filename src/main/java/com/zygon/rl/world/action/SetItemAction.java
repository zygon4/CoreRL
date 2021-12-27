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
public class SetItemAction extends Action {

    private final Identifable identifable;
    private final Location location;

    public SetItemAction(Identifable identifable, Location location) {
        this.identifable = identifable;
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
