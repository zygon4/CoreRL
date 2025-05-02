/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;

/**
 * Explicitly player oriented.
 *
 * @author zygon
 */
public class DropPlayerItemAction extends Action {

    private final Item item;
    private final boolean equipped;

    public DropPlayerItemAction(final Item item, final boolean equipped) {
        this.item = item;
        this.equipped = equipped;
    }

    @Override
    public boolean canExecute(GameState state) {
        // Will throw exception if cannot drop.. that's a bug and should be fixed.
        return true;
    }

    @Override
    public GameState execute(GameState state) {
        CharacterSheet player = state.getWorld().getPlayer();

        if (canExecute(state)) {
            World world = state.getWorld();
            world.add(player.remove(item, equipped), world.getPlayerLocation());
            world.add(item, world.getPlayerLocation());
            return state.log("Dropped " + item.getName());
        }

        return state;
    }
}
