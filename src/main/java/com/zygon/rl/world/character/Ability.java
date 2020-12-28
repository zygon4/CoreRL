package com.zygon.rl.world.character;

// This is a bad package reference
import com.zygon.rl.game.GameState;

/**
 * Same as a skill.
 *
 * @author zygon
 */
public interface Ability {

    // TODO: single vs multiple, location e.g. ADJACENT, ROOM, RADIUS
    public enum Target {
        ADJACENT_LIVING,
        NONE,
        RANGED
    }

    /**
     * Returns the name of this ability.
     *
     * @return the name of this ability.
     */
    String getName();

    /**
     * Returns the context when this ability is available.
     *
     * @return the context when this ability is available.
     */
    String availableContext();

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
     * @return the GameState after using this ability.
     */
    GameState use(GameState state); // TODO: location or entiity
}
