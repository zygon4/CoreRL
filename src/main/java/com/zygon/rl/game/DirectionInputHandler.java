package com.zygon.rl.game;

import com.zygon.rl.world.Location;

import java.util.HashSet;
import java.util.Set;

import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_1;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_2;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_3;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_4;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_5;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_6;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_7;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_8;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_9;

/**
 * Handles directions, could be used for spells/other actions in the future.
 * There's no specific target, just a direction.
 *
 * @author zygon
 */
public abstract class DirectionInputHandler extends BaseInputHandler {

    private final Location centralLocation;

    public DirectionInputHandler(GameConfiguration gameConfiguration,
            Location centralLocation, Set<Input> additional) {
        super(gameConfiguration, addInputs(additional));

        this.centralLocation = centralLocation;
    }

    public Location getCentralLocation() {
        return centralLocation;
    }

    @Override
    public String getDisplayText(Input input) {
        // is this good enough for now?
        return input.toString();
    }

    protected Location getTarget(Location centralLocation, Input input) {
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

        return target;
    }

    protected Location getTarget(Input input) {
        return getTarget(getCentralLocation(), input);
    }

    @Override
    public GameState handleInvalidInput(GameState state) {
        return popInputContext(state);
    }

    private static Set<Input> addInputs(Set<Input> additional) {
        return addInputs(INPUTS_1_9, additional);
    }

    protected static Set<Input> addInputs(Set<Input> i1, Set<Input> i2) {
        Set<Input> inputs = new HashSet<>(i1);
        inputs.addAll(i2);
        return inputs;
    }

    protected static GameState popInputContext(GameState state) {
        return state.copy()
                .removeInputContext()
                .build();
    }
}
