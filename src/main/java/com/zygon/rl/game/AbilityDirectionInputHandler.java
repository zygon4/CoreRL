package com.zygon.rl.game;

import java.util.Optional;

import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.AbilityActionSet;

/**
 * Handles ability directions, could be used for spells/other actions in the
 * future. There's no specific target, just a direction. The ability impl will
 * decide if there's a legal target or not.
 *
 * @author zygon
 */
public class AbilityDirectionInputHandler extends BaseInputHandler {

    private final Ability ability;
    private final Location centralLocation;

    public AbilityDirectionInputHandler(GameConfiguration gameConfiguration,
            Ability ability, Location centralLocation) {
        super(gameConfiguration, INPUTS_1_9);

        this.ability = ability;
        this.centralLocation = centralLocation;
    }

    @Override
    public GameState apply(GameState state, Input input) {

        GameState newState = state;
        Location target = null;

        switch (convert(input)) {
            case NUMPAD_1, DIGIT_1 -> {
                target = Location.create(centralLocation.getX() - 1,
                        centralLocation.getY() - 1);
            }
            case NUMPAD_2, DIGIT_2 -> {
                target = Location.create(centralLocation.getX(),
                        centralLocation.getY() - 1);
            }
            case NUMPAD_3, DIGIT_3 -> {
                target = Location.create(centralLocation.getX() + 1,
                        centralLocation.getY() - 1);
            }
            case NUMPAD_4, DIGIT_4 -> {
                target = Location.create(centralLocation.getX() - 1,
                        centralLocation.getY());
            }
            case NUMPAD_5, DIGIT_5 -> {
                target = centralLocation;
            }
            case NUMPAD_6, DIGIT_6 -> {
                target = Location.create(centralLocation.getX() + 1,
                        centralLocation.getY());
            }
            case NUMPAD_7, DIGIT_7 -> {
                target = Location.create(centralLocation.getX() - 1,
                        centralLocation.getY() + 1);
            }
            case NUMPAD_8, DIGIT_8 -> {
                target = Location.create(centralLocation.getX(),
                        centralLocation.getY() + 1);
            }
            case NUMPAD_9, DIGIT_9 -> {
                target = Location.create(centralLocation.getX() + 1,
                        centralLocation.getY() + 1);
            }
        }

        if (target != null) {
            AbilityActionSet abilityActions = ability.use(state, Optional.empty(), Optional.of(target));
            for (Action abilityAction : abilityActions.actions()) {
                if (abilityAction.canExecute(state)) {
                    newState = abilityAction.execute(state);
                } else {
                    // stop if anything can't execute..
                    break;
                }
            }
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
        // is this good enough for now?
        return input.toString();
    }

    protected GameState popInputContext(GameState state) {
        return state.copy()
                .removeInputContext()// ability target
                .removeInputContext()// specific ability
                .removeInputContext()// ability menu
                .build();
    }
}
