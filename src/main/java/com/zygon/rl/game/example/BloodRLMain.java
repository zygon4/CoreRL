/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.game.example;

import com.zygon.rl.game.InputHandler;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameUI;
import com.zygon.rl.game.Input;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Region;
import com.zygon.rl.world.Regions;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

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

    // This is the basic way to customize the game context. Check what the
    // current context is and then utilize it and/or push/pop more context on
    // the stack.
    private static final class BloodActionSupplier extends InputHandler {

        @Override
        public GameState doApply(final GameState state, Input input) {
            GameState.Builder copy = state.copy();

            KeyCode inputKeyCode = convert(input);
            Stack<GameState.InputContext> inputContext = state.getInputContext();

            switch (inputContext.peek().getName()) {
                case "OUTER":
                    switch (inputKeyCode) {
                        case NUMPAD_5 -> {
                            System.out.println("Waiting " + input.getInput());
                            break;
                        }
                        case NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, /* NOT 5*/
                                NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9 -> {
                            // TODO: find player, check if location is available,
                            System.out.println("Moving " + input.getInput());
                            Location playerLoc = state.getRegions().find(Entities.PLAYER).iterator().next();

//                            state.getRegions().move(entity, destination);
                        }
                        case KEY_B ->
                            copy = copy.addInputContext(GameState.InputContext.builder()
                                    .setName("BITE").build());
                        default -> {
                            invalidInput(input);
                            // invalid but keep context as is
                        }
                    }

                    // move player
                    break;
                case "BITE":
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
                    copy = copy.removeInputContext();
                    break;

            }

            return copy.build();
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

        InputHandler inputHandler = new BloodActionSupplier();

        // Action pub/sub got sidelined for work on the input context
//        BiFunction<GameState, Action, GameState> actionLogger = new ActionLogger();
//        BiFunction<GameState, Action, GameState> actionExecutor = new ActionExecutor();
        // TODO: action persistance
//        ActionPublisher actionPublisher = new ActionPublisher(List.of(actionLogger, actionExecutor));
//
        // No zero/0 key
        Set<Input> numberDirectionKeys = getInputs(Set.of(
                KeyCode.NUMPAD_1, KeyCode.NUMPAD_2, KeyCode.NUMPAD_3,
                KeyCode.NUMPAD_4, KeyCode.NUMPAD_5, KeyCode.NUMPAD_6,
                KeyCode.NUMPAD_7, KeyCode.NUMPAD_8, KeyCode.NUMPAD_9));

        Set<Input> outerWorldInputs = new HashSet<>();
        outerWorldInputs.addAll(numberDirectionKeys);
        outerWorldInputs.add(Input.valueOf(KeyCode.KEY_B.getCode()));

        GameState.InputContext initialGameContext = GameState.InputContext.builder()
                .setName("OUTER")
                // TODO: syntax sugar on this
                .setValidInputs(outerWorldInputs)
                .build();

        Regions regions = Regions.create();
        Region region = new Region();

        for (int y = 50; y < 150; y++) {
            for (int x = 50; x < 150; x++) {
                region = region.add(Entities.DIRT, Location.create(x, y));
            }
        }

        region = region.add(Entities.PLAYER, Location.create(110, 110));
        regions = regions.add(region);

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
