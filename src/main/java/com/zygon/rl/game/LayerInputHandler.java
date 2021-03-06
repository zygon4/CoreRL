/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game;

import java.util.Set;
import java.util.function.BiFunction;

/**
 * "Layer" just meaning it's stacked with other input handlers in the context.
 * There's probably a better name.
 *
 * @author zygon
 */
public interface LayerInputHandler extends BiFunction<GameState, Input, GameState> {

    /**
     * Returns the valid inputs for this layer input handler.
     *
     * @return
     */
    Set<Input> getInputs();

    /**
     * Returns the display text for this input.
     *
     * @param input the input
     * @return the display text for this input.
     */
    String getDisplayText(Input input);

    /**
     * Returns the game state after an invalid input occurs.
     *
     * @param state
     * @return
     */
    default GameState handleInvalidInput(GameState state) {
        return state.copy()
                .removeInputContext()
                .build();
    }
}
