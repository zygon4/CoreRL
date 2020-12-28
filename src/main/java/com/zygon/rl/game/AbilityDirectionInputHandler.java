package com.zygon.rl.game;

import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.Ability;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles ability directions, could be used for spells/other actions in the
 * future. There's no specific target, just a direction. The ability impl will
 * decide if there's a legal target or not.
 *
 * @author zygon
 */
public class AbilityDirectionInputHandler extends BaseInputHandler {

    private final Ability ability;
    private final UUID entityId;

    public AbilityDirectionInputHandler(GameConfiguration gameConfiguration,
            Ability ability, UUID entityId) {
        super(gameConfiguration, INPUTS_1_9);

        this.ability = ability;
        this.entityId = entityId;
    }

    @Override
    public GameState apply(GameState state, Input input) {

        // TODO: convert the input (0-9) to a location relative to the player
        GameState newState = state;

        Entity centralEntity = state.getWorld().get(entityId);
        Location target = null;

        // TODO: should add direction and relative direction concepts?
        switch (convert(input)) {
            case NUMPAD_1, DIGIT_1 -> {
                target = Location.create(centralEntity.getLocation().getX() - 1,
                        centralEntity.getLocation().getY() - 1);
            }
            case NUMPAD_2, DIGIT_2 -> {
                target = Location.create(centralEntity.getLocation().getX(),
                        centralEntity.getLocation().getY() - 1);
            }
            case NUMPAD_3, DIGIT_3 -> {
                target = Location.create(centralEntity.getLocation().getX() + 1,
                        centralEntity.getLocation().getY() - 1);
            }
            case NUMPAD_4, DIGIT_4 -> {
                target = Location.create(centralEntity.getLocation().getX() - 1,
                        centralEntity.getLocation().getY());
            }
            case NUMPAD_5, DIGIT_5 -> {
                target = centralEntity.getLocation();
            }
            case NUMPAD_6, DIGIT_6 -> {
                target = Location.create(centralEntity.getLocation().getX() + 1,
                        centralEntity.getLocation().getY());
            }
            case NUMPAD_7, DIGIT_7 -> {
                target = Location.create(centralEntity.getLocation().getX() - 1,
                        centralEntity.getLocation().getY() + 1);
            }
            case NUMPAD_8, DIGIT_8 -> {
                target = Location.create(centralEntity.getLocation().getX(),
                        centralEntity.getLocation().getY() + 1);
            }
            case NUMPAD_9, DIGIT_9 -> {
                target = Location.create(centralEntity.getLocation().getX() + 1,
                        centralEntity.getLocation().getY() + 1);
            }
        }

        if (target != null) {
            newState = ability.use(state, Optional.empty(), Optional.of(target));
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

    private static GameState popInputContext(GameState state) {
        return state.copy()
                .removeInputContext()// ability target
                .removeInputContext()// specific ability
                .removeInputContext()// ability menu
                .build();
    }
}
