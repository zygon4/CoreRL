package com.zygon.rl.game;

import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author zygon
 */
public interface Behavior {

    // I think this needs more info??
    Action get(CharacterSheet character);
}
