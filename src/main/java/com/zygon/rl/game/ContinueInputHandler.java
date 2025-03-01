package com.zygon.rl.game;

import java.util.LinkedHashSet;
import java.util.Set;

import org.hexworks.zircon.api.uievent.KeyCode;

// see InventoryInputHandler
public final class ContinueInputHandler extends BaseInputHandler {

    private static Set<Input> createInputs() {
        Set<Input> inputs = new LinkedHashSet<>();
        inputs.add(Input.valueOf(KeyCode.ESCAPE.getCode()));
        inputs.add(Input.valueOf(KeyCode.SPACE.getCode()));
        return inputs;
    }

    private ContinueInputHandler(GameConfiguration gameConfiguration) {
        super(gameConfiguration, createInputs());
    }

    public static final ContinueInputHandler create(
            GameConfiguration gameConfiguration) {
        return new ContinueInputHandler(gameConfiguration);
    }

    @Override
    public GameState apply(final GameState state, Input input) {

        GameState newState = state;

        switch (convert(input)) {
            case ESCAPE, SPACE -> {
                newState = newState.copy()
                        .removeInputContext()
                        .setNotification(null)
                        .build();
            }
        }

        return newState;
    }

    /**
     * Override the default behavior and do *NOT* auto-pop the context on
     * invalid input.
     *
     * @param state
     * @return
     */
    @Override
    public GameState handleInvalidInput(GameState state) {
        return state;
    }

    @Override
    public String getDisplayText(Input input) {
        return input.toString();
    }
}
