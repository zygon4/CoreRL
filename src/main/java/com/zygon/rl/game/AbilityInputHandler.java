package com.zygon.rl.game;

import com.zygon.rl.data.Element;
import com.zygon.rl.world.character.Ability;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

final class AbilityInputHandler extends BaseInputHandler {

    private final Map<Input, Ability> abilitiesByKeyCode;

    private AbilityInputHandler(GameConfiguration gameConfiguration,
            Map<Input, Ability> abilitiesByKeyCode) {
        super(gameConfiguration, abilitiesByKeyCode.keySet());
        this.abilitiesByKeyCode = abilitiesByKeyCode;
    }

    // organizes the ability itself, not the targets
    // E.g. keyboard 'A' could activate the "throw" menu
    public static final AbilityInputHandler create(GameConfiguration gameConfiguration,
            Set<Ability> abilities) {
        Map<Input, Ability> inputs = createAlphaInputs(abilities);
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
                                .setName("TARGET")
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
                Set<Element> livingAdjacents = state.getWorld().getPlayerLocation().getNeighbors().stream()
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
            case NONE ->
                // no target, use ability and pop context.
                newState = ability.use(state, null, null).copy()
                        .removeInputContext()
                        .build();
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
