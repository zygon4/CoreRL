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
public class DropItemAction extends Action {

    private final Item item;

    public DropItemAction(Item item) {
        this.item = item;
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
            world.add(player.remove(item), world.getPlayerLocation());

            world.add(item.getTemplate(), world.getPlayerLocation());
            return state.log("Dropped " + item.getName());
        }

        return state;
    }
}
