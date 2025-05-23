package com.zygon.rl.world.character;

// This is a bad package reference
import java.util.Optional;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;

/**
 * Same as a skill.
 *
 * TODO: Costs for certain abilities, sound effects
 *
 * @author zygon
 */
public interface Ability {

    /**
     * The optional cost to initiate this ability.
     */
    public static record Cost(String pool, int ammount) {

        public static Cost create(String pool, int ammount) {
            return new Cost(pool, ammount);
        }
    }

    public enum Target {
        ADJACENT,
        ADJACENT_LIVING,
        NONE,
        RANGED // range vs ranged
    }

    /**
     * Returns the {@link Cost} to initiate this ability.
     *
     * @return the {@link Cost} to initiate this ability.
     */
    default Optional<Cost> getCost() {
        return Optional.empty();
    }

    /**
     * Returns the name of this ability.
     *
     * @return the name of this ability.
     */
    String getName();

    /**
     * Returns the Ability description.
     *
     * @return the Ability description.
     */
    String getDescription();

    /**
     * Returns the proficiency associated with this ability. <br> Could be a set
     * of proficiencies?
     *
     * @return the proficiency associated with this ability.
     */
    default Optional<String> getProficiencyId() {
        return Optional.empty();
    }

    /**
     * Returns the way in which this ability targets.
     *
     * @return the way in which this ability targets.
     */
    Target getTargeting();

    /**
     * Returns the {@link AbilityActionSet} after using this ability.
     *
     * @param state
     * @param targetEntity
     * @param targetLocation
     * @return the {@link AbilityActionSet} after using this ability.
     */
    AbilityActionSet use(GameState state, Optional<Identifable> targetEntity,
            Optional<Location> targetLocation);
}
