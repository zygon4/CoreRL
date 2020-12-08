/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game;

import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_1;
import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_2;
import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_3;
import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_4;
import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_5;
import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_6;
import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_7;
import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_8;
import static org.hexworks.zircon.api.uievent.KeyCode.NUMPAD_9;

/**
 *
 * @author zygon
 */
public abstract class BaseInputHandler implements LayerInputHandler {

    private static final System.Logger logger = System.getLogger(BaseInputHandler.class.getCanonicalName());

    protected static final Set<Input> INPUTS_1_9 = Set.of(
            Input.valueOf(NUMPAD_1.getCode()),
            Input.valueOf(NUMPAD_2.getCode()),
            Input.valueOf(NUMPAD_3.getCode()),
            Input.valueOf(NUMPAD_4.getCode()),
            Input.valueOf(NUMPAD_5.getCode()),
            Input.valueOf(NUMPAD_6.getCode()),
            Input.valueOf(NUMPAD_7.getCode()),
            Input.valueOf(NUMPAD_8.getCode()),
            Input.valueOf(NUMPAD_9.getCode()));

    private static final Map<Integer, KeyCode> keyCodesByInt = new HashMap<>();

    static {
        for (KeyCode kc : KeyCode.values()) {
            keyCodesByInt.put(kc.getCode(), kc);
        }
    }

    private final Set<Input> inputs;

    protected BaseInputHandler(Set<Input> inputs) {
        this.inputs = inputs != null
                ? Collections.unmodifiableSet(inputs) : Collections.emptySet();
    }

    // composes the inputs and returns a chain of inputs starting with this
    public LayerInputHandler compose(LayerInputHandler layerInputHandler) {

        if (!Collections.disjoint(this.getInputs(), layerInputHandler.getInputs())) {
            throw new IllegalArgumentException("Collections have overlapping inputs");
        }

        return new LayerInputHandler() {
            @Override
            public Set<Input> getInputs() {
                Set<Input> all = new HashSet<>(BaseInputHandler.this.getInputs());
                all.addAll(layerInputHandler.getInputs());
                return all;
            }

            @Override
            public GameState apply(GameState t, Input u) {
                Set<Input> thisInputs = BaseInputHandler.this.getInputs();

                if (thisInputs.contains(u)) {
                    return BaseInputHandler.this.apply(t, u);
                } else {
                    return layerInputHandler.apply(t, u);
                }
            }
        };
    }

    @Override
    public final Set<Input> getInputs() {
        return inputs;
    }

    // This bleeds out the zircon API. This basically says this is a zircon
    // "core" jar.
    protected final KeyCode convert(Input input) {
        return keyCodesByInt.get(input.getInput());
    }

    protected final void invalidInput(Input input) {
        logger.log(System.Logger.Level.INFO, input);
    }
}
