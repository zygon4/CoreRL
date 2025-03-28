package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 *
 * @author zygon
 */
public class SetCharacterAction extends Action {

    private final CharacterSheet character;
    private final Location location;

    public SetCharacterAction(CharacterSheet character, Location location) {
        this.character = character;
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    public Location getLocation() {
        return location;
    }

    public CharacterSheet getCharacter() {
        return character;
    }

    @Override
    public GameState execute(GameState state) {

        // TBD: check if the character has an item from a quest?
        state.getWorld().add(character, location);

        return state;
    }
}
