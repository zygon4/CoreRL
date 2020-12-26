package com.zygon.rl.game;

import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.Objects;

import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_1;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_2;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_3;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_4;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_6;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_7;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_8;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_9;

public final class DefaultOuterActionSupplier extends BaseInputHandler {

    private final GameConfiguration gameConfiguration;

    public DefaultOuterActionSupplier(GameConfiguration gameConfiguration) {
        super(INPUTS_1_9);
        this.gameConfiguration = Objects.requireNonNull(gameConfiguration);
    }

    @Override
    public GameState apply(final GameState state, Input input) {
        GameState.Builder copy = state.copy();
        KeyCode inputKeyCode = convert(input);
        switch (inputKeyCode) {
            case NUMPAD_5, DIGIT_5 -> {
                // TODO: log
                //                    System.out.println("Waiting " + input.getInput());
                // TODO: needs a "tick the world" handle
                break;
            }
            case NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, /* NOT 5*/ NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9,
                 DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4, /* NOT 5*/ DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9 -> {
                // TODO: check if location is available, check for bump actions
                Entity player = state.getWorld().get(gameConfiguration.getPlayerUuid());
                Location playerLocation = player.getLocation();

                Location destination = getRelativeLocation(playerLocation, input);
                state.getWorld().move(player, destination);
            }
            default -> {
                invalidInput(input);
                // Invalid but keep context as is
            }
        }
        return copy.build();
    }

}
