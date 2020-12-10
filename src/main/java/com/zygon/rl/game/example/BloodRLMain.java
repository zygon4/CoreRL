/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game.example;

import com.zygon.rl.game.BaseInputHandler;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameUI;
import com.zygon.rl.game.Input;
import com.zygon.rl.game.InputHandler;
import com.zygon.rl.game.LayerInputHandler;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.RegionHelper;
import com.zygon.rl.world.Regions;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hexworks.zircon.api.uievent.KeyCode.KEY_B;
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
 * This is a testing area until a new BloodRL2.0 project is made.
 *
 * @author zygon
 */
public class BloodRLMain {

    private static final class DefaultOuterActionSupplier extends BaseInputHandler {

        public DefaultOuterActionSupplier() {
            super(INPUTS_1_9);
        }

        @Override
        public GameState apply(final GameState state, Input input) {
            GameState.Builder copy = state.copy();

            KeyCode inputKeyCode = convert(input);

            switch (inputKeyCode) {
                case NUMPAD_5 -> {
                    System.out.println("Waiting " + input.getInput());
                    // TODO: needs a "tick the world" handle
                    break;
                }
                case NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, /* NOT 5*/
                     NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9 -> {
                    // TODO: find player, check if location is available, check for bump actions

                    Location playerLoc = state.getRegions().find(Entities.PLAYER).iterator().next();

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
                    System.out.println("Moving " + input.getInput() + " to " + destination);
                    copy.setRegions(state.getRegions().move(Entities.PLAYER, destination));
                }
                default -> {
                    invalidInput(input);
                    // Invalid but keep context as is
                }
            }

            return copy.build();
        }
    }

    private static final class BloodOuterActionSupplier extends BaseInputHandler {

        public BloodOuterActionSupplier() {
            super(Set.of(Input.valueOf(KEY_B.getCode())));
        }

        @Override
        public GameState apply(final GameState state, Input input) {
            GameState.Builder copy = state.copy();

            KeyCode inputKeyCode = convert(input);

            switch (inputKeyCode) {
                case KEY_B ->
                    copy = copy.addInputContext(GameState.InputContext.builder()
                            .setName("BITE").build());
                default -> {
                    invalidInput(input);
                    // Invalid but keep context as is
                }
            }

            return copy.build();
        }
    }

    // This is the basic way to customize the game context. Check what the
    // current context is and then utilize it and/or push/pop more context on
    // the stack.
    private static final class BiteActionSupplier extends BaseInputHandler {

        public BiteActionSupplier() {
            super(INPUTS_1_9);
        }

        @Override
        public GameState apply(final GameState state, Input input) {
            GameState.Builder copy = state.copy();

            KeyCode inputKeyCode = convert(input);

            switch (inputKeyCode) {
                case NUMPAD_5 -> {
                    // special case
                    System.out.println("Biting yourself");
                    break;
                }
                case NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, /* NOT 5*/
                        NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9 ->
                    // TODO: find valid target
                    System.out.println("Biting " + input.getInput());
                default -> {
                    invalidInput(input);
                }
            }

            // Pop context either way
            return copy.removeInputContext().build();
        }
    }

    private static Set<Input> getInputs(Set<KeyCode> keys) {
        return keys.stream()
                .map(KeyCode::getCode)
                .map(Input::valueOf)
                .collect(Collectors.toSet());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace(System.err);
        });

        // TODO: goes in rl.game package??
        DefaultOuterActionSupplier defaultOuterActionSupplier = new DefaultOuterActionSupplier();
        BloodOuterActionSupplier bloodOuterActionSupplier = new BloodOuterActionSupplier();
        LayerInputHandler composed = defaultOuterActionSupplier.compose(bloodOuterActionSupplier);

        // Compose "default" actions with the outer action
        Map<String, LayerInputHandler> of = Map.of(
                "DEFAULT", composed,
                "BITE", new BiteActionSupplier()
        );

        // need to compose defaults and customs
        InputHandler inputHandler = new InputHandler(of);

        // No zero/0 key
        Set<Input> numberDirectionKeys = getInputs(Set.of(
                KeyCode.NUMPAD_1, KeyCode.NUMPAD_2, KeyCode.NUMPAD_3,
                KeyCode.NUMPAD_4, KeyCode.NUMPAD_5, KeyCode.NUMPAD_6,
                KeyCode.NUMPAD_7, KeyCode.NUMPAD_8, KeyCode.NUMPAD_9));

        Set<Input> outerWorldInputs = new HashSet<>();
        outerWorldInputs.addAll(numberDirectionKeys);
        outerWorldInputs.add(Input.valueOf(KeyCode.KEY_B.getCode()));

        GameState.InputContext initialGameContext = GameState.InputContext.builder()
                .setName("DEFAULT")
                // TODO: syntax sugar on this
                .setValidInputs(outerWorldInputs)
                .build();

        // Create the world..
        RegionHelper regionHelper = new RegionHelper();
        Regions regions = Regions.create();

        // This creates the initial map
        for (int y = 0; y < 400; y += 20) {
            for (int x = 0; x < 400; x += 20) {
                Location loc = Location.create(x, y);
                boolean addPlayer = x == 200 && y == 200;
                regions = regions.add(regionHelper.generateRegion(loc, 20, 20, addPlayer));
            }
        }

        if (regions.find(Entities.PLAYER).isEmpty()) {
            throw new IllegalStateException("No player generated");
        }

        GameState initialState = GameState.builder()
                .addInputContext(initialGameContext)
                .setRegions(regions) // todo full region
                .build();

        GameConfiguration config = new GameConfiguration();
        config.setName("BloodRL");

        Game game = Game.builder()
                .setInputHandler(inputHandler)
                .setConfiguration(config)
                .setState(initialState)
                .build();

        GameUI ui = new GameUI(game);
        ui.start();
    }
}
