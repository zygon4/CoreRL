/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game.example;

import com.zygon.rl.game.BaseInputHandler;
import com.zygon.rl.game.DefaultOuterActionSupplier;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.game.GameUI;
import com.zygon.rl.game.Input;
import com.zygon.rl.game.InputHandler;
import com.zygon.rl.game.LayerInputHandler;
import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.BooleanAttribute;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Player;
import com.zygon.rl.world.World;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    // TODO:
    private static final class PlayerHealth extends GameSystem {

        public PlayerHealth() {
        }

        @Override
        public GameState apply(GameState t) {
            return t;
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
    public static void main(String[] args) throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace(System.err);
        });

        // Testing code for the Noise generator
//        double min = Double.MAX_VALUE;
//        double max = Double.NEGATIVE_INFINITY;
//
//        for (double y = 1.0; y < 80.0; y += 1.0) {
//            for (double x = 1.0; x < 60.0; x += 1.0) {
//
//                double val = Noise.noise(((double) x / 800l), ((double) y / 800l));
//
//                if (val > max) {
//                    max = val;
//                }
//                if (val < min) {
//                    min = val;
//                }
//
//                if (val < -0.10) {
//                    System.out.print("~");
//                } else if (val < -0.05) {
//                    System.out.print("s");
//                } else if (val < -0.0) {
//                    System.out.print(";");
//                } else if (val < 0.05) {
//                    System.out.print(".");
//                } else if (val < 0.10) {
//                    System.out.print("4");
//                } else {
//                    System.out.print("M");
//                }
//
////                System.out.printf("%f ", val);
//            }
//            System.out.println();
//        }
//
//        System.out.println("min: " + min);
//        System.out.println("max: " + max);
//
//        System.out.println("diff: " + (max - min));
//
//        /*
//        From: https://digitalfreepen.com/2017/06/20/range-perlin-noise.html
//        The N-dimensional Perlin noise has values in the range of [−√N4,√N4]. It can be shown that the arrows
//        must point to the center in N-dimension Perlin noise via induction reusing the steps of the 2D Perlin
//        noise proof.
//         */
//        if (min < -0.7071067811865475244 || max > 0.7071067811865475244) {
//            throw new IllegalStateException("min " + min + " max " + max);
//        }
//
//        if (true) {
//            return;
//        }
        // audio seems to slow the game down a lot??
//        Audio audio = new Audio(Paths.get("/home/zygon/src/github/CoreRL/audio.wav"));
//        audio.play();
        GameConfiguration config = new GameConfiguration();
        config.setGameName("BloodRL");
        config.setNpcSpawnRate(0.00000001);
        config.setPlayerUuid(UUID.randomUUID());

        DefaultOuterActionSupplier defaultOuterActionSupplier = new DefaultOuterActionSupplier(config);
        BloodOuterActionSupplier bloodOuterActionSupplier = new BloodOuterActionSupplier();
        LayerInputHandler composed = defaultOuterActionSupplier.compose(bloodOuterActionSupplier);

        // Compose "default" actions with the outer action
        Map<String, LayerInputHandler> of = Map.of(
                "DEFAULT", composed,
                "BITE", new BiteActionSupplier()
        );

        // need to compose defaults and customs
        InputHandler inputHandler = new InputHandler(of);

        GameState.InputContext initialGameContext = GameState.InputContext.builder()
                .setName("DEFAULT")
                .build();

        Player player = Player.create(Entities.PLAYER
                .copy()
                .setId(config.getPlayerUuid())
                .setName("Joe")
                .setDescription("fierce something")
                .setLocation(Location.create(0, 0))
                .addAttributes(Attribute.builder()
                        .setName(CommonAttributes.HEALTH.name())
                        .setDescription("Health")
                        .setValue("85")
                        .build())
                .addAttributes(BooleanAttribute.create(Attribute.builder()
                        .setName(CommonAttributes.LIVING.name()).build(), true))
                .build()).build();

        World world = new World();
        world.add(player.getEntity());

        GameState initialState = GameState.builder()
                .addInputContext(initialGameContext)
                .setWorld(world)
                .build();

        Game game = Game.builder()
                .addGameSystem(new PlayerHealth())
                .setInputHandler(inputHandler)
                .setConfiguration(config)
                .setState(initialState)
                .build();

        GameUI ui = new GameUI(game);
        ui.start();
    }
}
