package com.zygon.rl.game;

import com.zygon.rl.world.Location;
import org.hexworks.zircon.api.uievent.KeyCode;

public final class DefaultOuterActionSupplier extends BaseInputHandler {

    public DefaultOuterActionSupplier() {
        super(INPUTS_1_9);
    }

    @Override
    public GameState apply(final GameState state, Input input) {
        GameState.Builder copy = state.copy();
        KeyCode inputKeyCode = convert(input);
        switch (inputKeyCode) {
            case NUMPAD_5 -> {
                // TODO: log
                //                    System.out.println("Waiting " + input.getInput());
                // TODO: needs a "tick the world" handle
                break;
            }
            case NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, /* NOT 5*/ NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9 -> {
                // TODO: check if location is available, check for bump actions
                Location playerLoc = state.getPlayerLocation();
                int nextX = playerLoc.getX();
                int nextY = playerLoc.getY();
                int nextZ = playerLoc.getZ();
                switch (inputKeyCode) {
                    case NUMPAD_1 -> {
                        nextX--;
                        nextY--;
                    }
                    case NUMPAD_2 ->
                        nextY--;
                    case NUMPAD_3 -> {
                        nextX++;
                        nextY--;
                    }
                    case NUMPAD_4 ->
                        nextX--;
                    case NUMPAD_6 ->
                        nextX++;
                    case NUMPAD_7 -> {
                        nextX--;
                        nextY++;
                    }
                    case NUMPAD_8 ->
                        nextY++;
                    case NUMPAD_9 -> {
                        nextX++;
                        nextY++;
                    }
                }
                Location destination = Location.create(nextX, nextY, nextZ);
                copy.setPlayerLocation(destination);
            }
            default -> {
                invalidInput(input);
                // Invalid but keep context as is
            }
        }
        return copy.build();
    }

}
