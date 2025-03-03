/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.CorpseItem;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author djc
 */
public class DeathAction extends Action {

    private final CharacterSheet dead;
    private final Location location;

    public DeathAction(CharacterSheet dead, Location location) {
        this.dead = dead;
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {
        return Boolean.TRUE;
    }

    @Override
    public GameState execute(GameState state) {
        state = state.log(dead.getName() + " died!");
        state.getWorld().remove(dead, location);
        Item corpse = CorpseItem.create(dead);
        state.getWorld().add(corpse, location);
        return state;
    }
}
