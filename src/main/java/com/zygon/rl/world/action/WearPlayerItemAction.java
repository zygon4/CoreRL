/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.Armor;
import com.zygon.rl.world.character.CharacterSheet;

/**
 * Explicitly player oriented.
 *
 * @author zygon
 */
public class WearPlayerItemAction extends Action {

    private final Armor armor;

    public WearPlayerItemAction(final Armor armor) {
        this.armor = armor;
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
            world.add(player.equip(armor), world.getPlayerLocation());
            return state.log("Wearing " + armor.getName());
        }

        return state;
    }
}
