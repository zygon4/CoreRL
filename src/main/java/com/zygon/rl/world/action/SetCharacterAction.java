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

    private final Location location;
    private final CharacterSheet sheet;

    public SetCharacterAction(Location location, CharacterSheet sheet) {
        this.location = location;
        this.sheet = sheet;
    }

    @Override
    public boolean canExecute(GameState state) {
        return true;
    }

    @Override
    public GameState execute(GameState state) {
        state.getWorld().add(sheet, location);

        return state;
    }
}
