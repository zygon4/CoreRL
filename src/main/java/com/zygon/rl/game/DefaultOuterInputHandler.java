package com.zygon.rl.game;

import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.MeleeAttackAction;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Equipment;
import com.zygon.rl.world.character.Weapon;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.HashSet;
import java.util.Set;

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
        // 'I' for inventory
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_I.getCode()));
        // ESC for game menu
        defaultKeyCodes.add(Input.valueOf(KeyCode.ESCAPE.getCode()));
    }

    public DefaultOuterInputHandler(GameConfiguration gameConfiguration) {
        super(gameConfiguration, defaultKeyCodes);
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
                CharacterSheet character = state.getWorld().getPlayer();

                AbilityInputHandler abilityHandler = AbilityInputHandler.create(
                        getGameConfiguration(), character.getAbilities());

                copy = copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("ABILITY")
                                .setHandler(abilityHandler)
                                .setPrompt(GameState.InputContextPrompt.LIST)
                                .build());
            }
            // ability - present abilities
            case KEY_I -> {
                CharacterSheet character = state.getWorld().getPlayer();
                Equipment eq = character.getEquipment();

                StringBuilder eqSb = new StringBuilder();

                Weapon weapon = eq.getEquippedWeapon();

                if (weapon != null) {
                    eqSb.append(eq.getEquippedWeapon()).append(" [RIGHT HAND]");
                }

                System.out.println(eqSb.toString());
                // TODO: use built-in modals/lists. THis is esspecially important
                // because this style of context interaction won't cause a pause
                // for the game so you'll get hit while using a menu. This is bad.
            }
            case NUMPAD_5, DIGIT_5 -> {
                // TODO: wait action
                copy.addLog("Waiting");
                copy.setWorld(state.getWorld()
                        .setCalendar(state.getWorld().getCalendar().addTime(DEFAULT_ACTION_TIME)));
                break;
            }
            case NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, /* NOT 5*/ NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9,
                 DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4, /* NOT 5*/ DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9 -> {

                final World world = state.getWorld();
                CharacterSheet player = world.getPlayer();
                Location playerLocation = world.getPlayerLocation();

                Location destination = getRelativeLocation(playerLocation, input);
                if (state.getWorld().canMove(destination)) {
                    state.getWorld().move(player, playerLocation, destination);
                } else {
                    // TODO: bump to interact
                    CharacterSheet interactable = state.getWorld().get(destination);

                    // TODO: hostile status
                    if (interactable != null) {
                        Action bumpAttack = new MeleeAttackAction(world,
                                getGameConfiguration(), player, interactable, destination);

                        if (bumpAttack.canExecute()) {
                            bumpAttack.execute();
                        }

                        // TODO: resolve damages, kill NPCs or player
                    }
                    // else an item?
                }

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
