/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game;

import java.util.Set;
import java.util.function.BiFunction;

/**
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
}
