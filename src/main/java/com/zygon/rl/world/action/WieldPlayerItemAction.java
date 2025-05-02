/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Weapon;

/**
 * Explicitly player oriented.
 *
 * @author zygon
 */
public class WieldPlayerItemAction extends Action {

    private final Weapon weapon;

    public WieldPlayerItemAction(final Weapon weapon) {
        this.weapon = weapon;
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
            world.add(player.wield(weapon), world.getPlayerLocation());
            return state.log("Wielding " + weapon.getName());
        }

        return state;
    }
}
