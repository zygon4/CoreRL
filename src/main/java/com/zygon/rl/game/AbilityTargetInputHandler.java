package com.zygon.rl.game;

import com.zygon.rl.data.Element;
import com.zygon.rl.world.character.Ability;

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
    private final Map<Input, Element> targetsByInput;

    private AbilityTargetInputHandler(GameConfiguration gameConfiguration,
            Ability ability, Map<Input, Element> targetsByInput) {
        super(gameConfiguration, targetsByInput.keySet());

        this.ability = ability;
        this.targetsByInput = targetsByInput;
    }

    public static final AbilityTargetInputHandler create(GameConfiguration gameConfiguration,
            Ability ability, Set<Element> targets) {

        // TODO: use location-based inputs vs alphabet
        Map<Input, Element> targetsByInput = createAlphaInputs(targets);
        return new AbilityTargetInputHandler(gameConfiguration, ability, targetsByInput);
    }

    @Override
    public GameState apply(GameState state, Input input) {

        GameState newState = state;
        Element target = targetsByInput.get(input);

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
        Element entity = targetsByInput.get(input);
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
