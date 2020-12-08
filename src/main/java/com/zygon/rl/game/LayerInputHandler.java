/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game;

import java.util.Collections;
import java.util.HashSet;
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

    // composes the inputs and returns a chain of inputs starting with this
    default LayerInputHandler compose(LayerInputHandler layerInputHandler) {

        if (!Collections.disjoint(this.getInputs(), layerInputHandler.getInputs())) {
            throw new IllegalArgumentException("Collections have overlapping inputs");
        }

        return new LayerInputHandler() {
            @Override
            public Set<Input> getInputs() {
                Set<Input> all = new HashSet<>(LayerInputHandler.this.getInputs());
                all.addAll(layerInputHandler.getInputs());
                return all;
            }

            @Override
            public GameState apply(GameState t, Input u) {
                Set<Input> thisInputs = LayerInputHandler.this.getInputs();

                if (thisInputs.contains(u)) {
                    return LayerInputHandler.this.apply(t, u);
                } else {
                    return layerInputHandler.apply(t, u);
                }
            }
        };
    }
}
