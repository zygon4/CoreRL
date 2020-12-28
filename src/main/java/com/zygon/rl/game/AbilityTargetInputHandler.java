package com.zygon.rl.game;

import com.zygon.rl.world.Entity;
import com.zygon.rl.world.character.Ability;

import java.util.Map;
import java.util.Set;

/**
 * Handles ability targets, could be used for spells/other actions in the
 * future.
 *
 * @author zygon
 */
public class AbilityTargetInputHandler extends BaseInputHandler {

    private final Ability ability;
    private final Map<Input, Entity> targetsByInput;

    private AbilityTargetInputHandler(GameConfiguration gameConfiguration,
            Ability ability, Map<Input, Entity> targetsByInput) {
        super(gameConfiguration, targetsByInput.keySet());

        this.ability = ability;
        this.targetsByInput = targetsByInput;
    }

    public static final AbilityTargetInputHandler create(GameConfiguration gameConfiguration,
            Ability ability, Set<Entity> targets) {

        // TODO: use location-based inputs vs alphabet
        Map<Input, Entity> targetsByInput = createAlphaInputs(targets);
        return new AbilityTargetInputHandler(gameConfiguration, ability, targetsByInput);
    }

    @Override
    public GameState apply(GameState state, Input input) {

        GameState newState = state;
        Entity target = targetsByInput.get(input);

        if (target != null) {
            // TODO:
            newState = ability.use(state);
        } else {
            invalidInput(input);
        }

        return newState.copy()
                .removeInputContext()
                .build();
    }
}
