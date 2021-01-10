package com.zygon.rl.world.character;

// This is a bad package reference
import com.zygon.rl.data.Element;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;

import java.util.Optional;

/**
 * Same as a skill.
 *
 * TODO: Costs for certain abilities, sound effects
 *
 * @author zygon
 */
public interface Ability {

    public enum Target {
        ADJACENT,
        ADJACENT_LIVING,
        NONE,
        RANGED // range vs ranged
    }

    /**
     * Returns the name of this ability.
     *
     * @return the name of this ability.
     */
    String getName();

    /**
     * Returns the way in which this ability targets.
     *
     * @return the way in which this ability targets.
     */
    Target getTargeting();

    /**
     * Returns the GameState after using this ability.
     *
     * @param state
     * @param targetEntity
     * @param targetLocation
     * @return the GameState after using this ability.
     */
    GameState use(GameState state, Optional<Element> targetEntity, Optional<Location> targetLocation);
}
