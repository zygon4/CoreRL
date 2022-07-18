package com.zygon.rl.world.character;

// This is a bad package reference
import com.zygon.rl.data.Identifable;
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
   * Returns the GameState after using this ability. TODO: consider returning a
   * "result" vs a state. Would be easier to implement - e.g. return "took this
   * much time", "these are the logs", "use this sound effect". However, a full
   * game state allows for complex abilities like time travel. Could have two
   * versions?
   *
   * @param state
   * @param targetEntity
   * @param targetLocation
   * @return the GameState after using this ability.
   */
  GameState use(GameState state, Optional<Identifable> targetEntity,
          Optional<Location> targetLocation);
}
