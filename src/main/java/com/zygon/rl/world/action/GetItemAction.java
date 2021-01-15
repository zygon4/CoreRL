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
public class GetItemAction extends Action {

    private final Item item;

    public GetItemAction(Item item) {
        this.item = item;
    }

    @Override
    public boolean canExecute(GameState state) {
        CharacterSheet player = state.getWorld().getPlayer();

        return canExecute(player);
    }

    @Override
    public GameState execute(GameState state) {
        CharacterSheet player = state.getWorld().getPlayer();

        if (canExecute(player)) {
            World world = state.getWorld();
            world.remove(item.getTemplate(), world.getPlayerLocation());
            world.add(player.add(item), world.getPlayerLocation());
            return state.log("Picked up " + item.getName());
        }

        return state;
    }

    private boolean canExecute(CharacterSheet player) {
        return player.getInventory().canAdd(item);
    }
}
