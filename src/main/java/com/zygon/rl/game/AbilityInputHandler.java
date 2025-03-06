package com.zygon.rl.game;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.zygon.rl.world.Tangible;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.AbilityActionSet;

final class AbilityInputHandler extends BaseInputHandler {

    private final Map<Input, Ability> abilitiesByKeyCode;

    private AbilityInputHandler(GameConfiguration gameConfiguration,
            Map<Input, Ability> abilitiesByKeyCode) {
        super(gameConfiguration, abilitiesByKeyCode.keySet());
        this.abilitiesByKeyCode = abilitiesByKeyCode;
    }

    // organizes the ability itself, not the targets
    // E.g. keyboard 'A' could activate the "throw" menu
    public static final AbilityInputHandler create(
            GameConfiguration gameConfiguration,
            Set<Ability> abilities) {
        Map<Input, Ability> inputs = createAlphaInputs(new ArrayList<>(abilities));
        return new AbilityInputHandler(gameConfiguration, inputs);
    }

    @Override
    public GameState apply(final GameState state, Input input) {
        Ability ability = abilitiesByKeyCode.get(input);

        GameState newState = state;

        switch (ability.getTargeting()) {
            case ADJACENT -> {
                newState = newState.copy()
                        .addInputContext(GameState.InputContext.builder()
                                .setName("ABILITY")
                                .setHandler(new AbilityDirectionInputHandler(
                                        getGameConfiguration(), ability, state.getWorld().getPlayerLocation()))
                                .setPrompt(GameState.InputContextPrompt.DIRECTION)
                                .build())
                        .build();
            }
            case ADJACENT_LIVING -> {
                // This isn't being used right now..

                // need to find all legal targets,
                // should this code be preparing them? or asking for them
                // from the game impl?
                Set<Tangible> livingAdjacents = state.getWorld().getPlayerLocation().getNeighbors().stream()
                        .map(loc -> state.getWorld().get(loc))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                newState = newState.copy()
                        .addInputContext(GameState.InputContext.builder()
                                .setName("TARGET")
                                .setHandler(AbilityTargetInputHandler.create(
                                        getGameConfiguration(), ability, livingAdjacents))
                                .setPrompt(GameState.InputContextPrompt.LIST)
                                .build())
                        .build();
            }
            case NONE -> {
                // no target, use ability and pop context.
                AbilityActionSet abilityActions = ability.use(state, Optional.empty(), Optional.empty());

                for (Action abilityAction : abilityActions.actions()) {
                    if (abilityAction.canExecute(state)) {
                        newState = abilityAction.execute(state);
                    } else {
                        // stop if anything can't execute..
                        break;
                    }
                }

                newState = newState.copy()
                        .removeInputContext()
                        .build();
            }
        }

        return newState;
    }

    @Override
    public String getDisplayText(Input input) {
        Ability ability = abilitiesByKeyCode.get(input);
        return ability.getName();
    }

    /*pkg*/ Map<Input, Ability> getAbilitiesByKeyCode() {
        return abilitiesByKeyCode;
    }
}
