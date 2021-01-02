package com.zygon.rl.game;

import java.util.Set;

import static org.hexworks.zircon.api.uievent.KeyCode.ESCAPE;

/**
 * @author zygon
 */
public class ModalInputHandler extends BaseInputHandler {

    public ModalInputHandler(GameConfiguration gameConfiguration) {
        super(gameConfiguration, Set.of(Input.valueOf(ESCAPE.getCode())));
    }

    @Override
    public GameState apply(GameState state, Input input) {

        GameState newState = state;

        switch (convert(input)) {
            case ESCAPE -> {
                newState = newState.copy()
                        .removeInputContext()
                        .build();
            }
        }

        return newState;
    }

    @Override
    public String getDisplayText(Input input) {
        // is this good enough for now?
        return input.toString();
    }

}
