package com.zygon.rl.game;

import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.Objects;

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

                int nextX = playerLocation.getX();
                int nextY = playerLocation.getY();
                int nextZ = playerLocation.getZ();
                switch (inputKeyCode) {
                    case NUMPAD_1, DIGIT_1 -> {
                        nextX--;
                        nextY--;
                    }
                    case NUMPAD_2, DIGIT_2 ->
                        nextY--;
                    case NUMPAD_3, DIGIT_3 -> {
                        nextX++;
                        nextY--;
                    }
                    case NUMPAD_4, DIGIT_4 ->
                        nextX--;
                    case NUMPAD_6, DIGIT_6 ->
                        nextX++;
                    case NUMPAD_7, DIGIT_7 -> {
                        nextX--;
                        nextY++;
                    }
                    case NUMPAD_8, DIGIT_8 ->
                        nextY++;
                    case NUMPAD_9, DIGIT_9 -> {
                        nextX++;
                        nextY++;
                    }
                }
                Location destination = Location.create(nextX, nextY, nextZ);
                copy.setWorld(state.getWorld().move(player, destination));
            }
            default -> {
                invalidInput(input);
                // Invalid but keep context as is
            }
        }
        return copy.build();
    }

}
