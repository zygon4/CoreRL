package com.zygon.rl.world.action;

import com.zygon.rl.world.World;

/**
 * Can be anything from move, to use ability, to simulate physics, to attack
 *
 * @author zygon
 */
public abstract class Action {

    private final World world;

    protected Action(World world) {
        this.world = world;
    }

    public abstract boolean canExecute();

    public abstract void execute();

    public final World getWorld() {
        return world;
    }
}
