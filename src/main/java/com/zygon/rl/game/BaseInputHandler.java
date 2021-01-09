/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game;

import com.zygon.rl.world.Location;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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
            Input.valueOf(NUMPAD_9.getCode()),
            Input.valueOf(DIGIT_1.getCode()),
            Input.valueOf(DIGIT_2.getCode()),
            Input.valueOf(DIGIT_3.getCode()),
            Input.valueOf(DIGIT_4.getCode()),
            Input.valueOf(DIGIT_5.getCode()),
            Input.valueOf(DIGIT_6.getCode()),
            Input.valueOf(DIGIT_7.getCode()),
            Input.valueOf(DIGIT_8.getCode()),
            Input.valueOf(DIGIT_9.getCode()));

    private static final Map<Integer, KeyCode> keyCodesByInt = new HashMap<>();

    static {
        for (KeyCode kc : KeyCode.values()) {
            keyCodesByInt.put(kc.getCode(), kc);
        }
    }

    private final GameConfiguration gameConfiguration;
    private final Set<Input> inputs;

    protected BaseInputHandler(GameConfiguration gameConfiguration, Set<Input> inputs) {
        this.gameConfiguration = Objects.requireNonNull(gameConfiguration);
        this.inputs = inputs != null
                ? Collections.unmodifiableSet(inputs) : Collections.emptySet();
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

    protected final GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    protected final Location getRelativeLocation(Location location, Input input) {

        int nextX = location.getX();
        int nextY = location.getY();
        int nextZ = location.getZ();

        KeyCode inputKeyCode = convert(input);
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
            default -> {
                invalidInput(input);
            }
        }

        return Location.create(nextX, nextY, nextZ);
    }

    // maps alphabet characters (ordered) as inputs to the elements provided.
    // If more characters are needed, it'll pull them from the KeyCode enum.
    protected static final <T> Map<Input, T> createAlphaInputs(Set<T> ts) {

        Map<Input, T> inputs = new LinkedHashMap<>();
        int index = KeyCode.KEY_A.getCode();

        for (T t : ts) {
            Input input = Input.valueOf(index++);
            inputs.put(input, t);
        }

        return inputs;
    }
}
