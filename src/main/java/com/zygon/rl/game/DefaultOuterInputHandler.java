package com.zygon.rl.game;

import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.Ability;
import com.zygon.rl.world.character.CharacterSheet;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_1;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_2;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_3;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_4;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_6;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_7;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_8;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_9;

public final class DefaultOuterInputHandler extends BaseInputHandler {

    private static final int DEFAULT_ACTION_TIME = 6; // seconds
    private static final Set<Input> defaultKeyCodes = new HashSet<>();

    static {
        // Movement keys
        defaultKeyCodes.addAll(INPUTS_1_9);
        // 'A' for abilities
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_A.getCode()));
        // ESC for game menu
        defaultKeyCodes.add(Input.valueOf(KeyCode.ESCAPE.getCode()));
    }

    private final Map<String, Ability> abilitiesByName;

    public DefaultOuterInputHandler(GameConfiguration gameConfiguration) {
        super(gameConfiguration, defaultKeyCodes);
        this.abilitiesByName = gameConfiguration.getCustomAbilities() != null
                ? gameConfiguration.getCustomAbilities().stream()
                        .collect(Collectors.toMap(k -> k.getName(), v -> v))
                : Collections.emptyMap();
    }

    @Override
    public GameState apply(final GameState state, Input input) {
        GameState.Builder copy = state.copy();
        KeyCode inputKeyCode = convert(input);
        switch (inputKeyCode) {
            case ESCAPE -> {
                copy = copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("GAME_MENU")
                                .setHandler(new ModalInputHandler(getGameConfiguration()))
                                .setPrompt(GameState.InputContextPrompt.MODAL)
                                .build());
            }
            // ability - present abilities
            case KEY_A -> {
                Entity playerEntity = getPlayer(state);
                CharacterSheet character = CharacterSheet.fromEntity(playerEntity);

                // As a (not terrible) hack, adding bite manually because it's
                // not serialized using the Entity class (yet).
                Set<Ability> abilities = new LinkedHashSet<>();
                abilities.addAll(character.getAbilities());
                abilities.add(abilitiesByName.get("Bite"));
                character = character.set(abilities);
                // Done with hack

                AbilityInputHandler abilityHandler = AbilityInputHandler.create(
                        getGameConfiguration(), character.getAbilities());

                copy = copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("ABILITY")
                                .setHandler(abilityHandler)
                                .setPrompt(GameState.InputContextPrompt.LIST)
                                .build());
            }
            case NUMPAD_5, DIGIT_5 -> {
                // TODO: log
                //                    System.out.println("Waiting " + input.getInput());
                copy.setWorld(state.getWorld()
                        .setCalendar(state.getWorld().getCalendar().addTime(DEFAULT_ACTION_TIME)));
                break;
            }
            case NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, /* NOT 5*/ NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9,
                 DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4, /* NOT 5*/ DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9 -> {
                // TODO: check if location is available, check for bump actions
                Entity player = getPlayer(state);
                Location playerLocation = player.getLocation();

                Location destination = getRelativeLocation(playerLocation, input);
                state.getWorld().move(player, destination);

                // Same movement cost everywhere for now..
                // Same movement speed, etc
                copy.setWorld(state.getWorld()
                        .setCalendar(state.getWorld().getCalendar().addTime(DEFAULT_ACTION_TIME)));
            }
            default -> {
                invalidInput(input);
                // Invalid but keep context as is
            }
        }
        return copy.build();
    }

    @Override
    public String getDisplayText(Input input) {
        KeyCode inputKeyCode = convert(input);
        switch (inputKeyCode) {
            case KEY_A -> {
                return "Abilities";
            }
            default -> {
                return inputKeyCode.name();
            }
        }
    }
}