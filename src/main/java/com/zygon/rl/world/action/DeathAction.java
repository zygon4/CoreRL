/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import java.util.Collection;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.CorpseItem;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.Armor;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Equipment;
import com.zygon.rl.world.character.Inventory;
import com.zygon.rl.world.character.Weapon;

/**
 *
 * @author djc
 */
public class DeathAction extends Action {

    private final CharacterSheet dead;
    private final Location location;
    private final String reason;

    public DeathAction(CharacterSheet dead, Location location,
            final String reason) {
        this.dead = dead;
        this.location = location;
        this.reason = reason;
    }

    @Override
    public boolean canExecute(GameState state) {
        return Boolean.TRUE;
    }

    @Override
    public GameState execute(GameState state) {
        state = state.log(dead.getName() + " died due to" + reason);

        Inventory inv = dead.getInventory();
        Equipment eq = dead.getEquipment();

        state.getWorld().remove(dead, location);
        Item corpse = CorpseItem.create(dead);
        state.getWorld().add(corpse, location);

        for (Item item : inv.getItems()) {
            state.getWorld().add(item, location);
        }

        for (Weapon weap : eq.getWeapons()) {
            state.getWorld().add(weap, location);
        }

        Collection<Armor> worn = eq.listEquipped();
        for (Armor a : worn) {
            state.getWorld().add(a, location);
        }

        return state;
    }
}
