package com.zygon.rl.game;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.world.Tangible;
import com.zygon.rl.world.character.Ability;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Handles ability targets, could be used for spells/other actions in the
 * future.
 *
 * @author zygon
 */
public class AbilityTargetInputHandler extends BaseInputHandler {

    private final Ability ability;
    private final Map<Input, Tangible> targetsByInput;

    private AbilityTargetInputHandler(GameConfiguration gameConfiguration,
            Ability ability, Map<Input, Tangible> targetsByInput) {
        super(gameConfiguration, targetsByInput.keySet());

        this.ability = ability;
        this.targetsByInput = targetsByInput;
    }

    public static final AbilityTargetInputHandler create(GameConfiguration gameConfiguration,
            Ability ability, Set<Tangible> targets) {

        // TODO: use location-based inputs vs alphabet
        Map<Input, Tangible> targetsByInput = createAlphaInputs(new ArrayList<>(targets));
        return new AbilityTargetInputHandler(gameConfiguration, ability, targetsByInput);
    }

    @Override
    public GameState apply(GameState state, Input input) {

        GameState newState = state;
        Identifable target = targetsByInput.get(input);

        if (target != null) {
            newState = ability.use(state, Optional.of(target), Optional.empty());
        } else {
            invalidInput(input);
        }

        return popInputContext(newState);
    }

    @Override
    public GameState handleInvalidInput(GameState state) {
        return popInputContext(state);
    }

    @Override
    public String getDisplayText(Input input) {
        Tangible entity = targetsByInput.get(input);
        return entity.getName();
    }

    private static GameState popInputContext(GameState state) {
        return state.copy()
                .removeInputContext()// ability target
                .removeInputContext()// specific ability
                .removeInputContext()// ability menu
                .build();
    }
}
