package com.zygon.rl.game;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.WorldElement;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.field.FieldData;
import com.zygon.rl.data.items.Building;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Field;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Tangible;
import com.zygon.rl.world.World;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.CloseDoorAction;
import com.zygon.rl.world.action.DropPlayerItemAction;
import com.zygon.rl.world.action.ExamineAction;
import com.zygon.rl.world.action.GetItemAction;
import com.zygon.rl.world.action.MeleeAttackAction;
import com.zygon.rl.world.action.MoveAction;
import com.zygon.rl.world.action.OpenDoorAction;
import com.zygon.rl.world.action.SetTangibleAction;
import com.zygon.rl.world.action.SoundEffectAction;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.InitiateDialog;

import org.apache.commons.math3.util.Pair;
import org.hexworks.zircon.api.uievent.KeyCode;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_1;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_2;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_3;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_4;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_6;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_7;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_8;
import static org.hexworks.zircon.api.uievent.KeyCode.DIGIT_9;
import static org.hexworks.zircon.api.uievent.KeyCode.ESCAPE;
import static org.hexworks.zircon.api.uievent.KeyCode.KEY_I;
import static org.hexworks.zircon.api.uievent.KeyCode.KEY_P;

public final class DefaultOuterInputHandler extends BaseInputHandler {

    private static final int DEFAULT_ACTION_TIME = 6; // seconds
    private static final Set<Input> defaultKeyCodes = new HashSet<>();

    static {
        // Movement keys
        defaultKeyCodes.addAll(INPUTS_1_9);
        // 'a' for abilities
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_A.getCode()));
        // 'c' for close adjacent
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_C.getCode()));
        // 'd' for drop
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_D.getCode()));
        // 'e' for examine adjacent
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_E.getCode()));
        // 'g' for get
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_G.getCode()));
        // 'i' for inventory
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_I.getCode()));
        // 'm' for map
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_M.getCode()));
        // 'p' for player
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_P.getCode()));
        // 't' for talk
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_T.getCode()));
        // 'q' for quests
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_Q.getCode()));
        // 'x' look around
        defaultKeyCodes.add(Input.valueOf(KeyCode.KEY_X.getCode()));

        // These are for testing spells out and how fields will act.. not permanent
        // However, i can see a hotkeys being assigned..
        defaultKeyCodes.add(Input.valueOf(KeyCode.F1.getCode()));
        defaultKeyCodes.add(Input.valueOf(KeyCode.F2.getCode()));
        defaultKeyCodes.add(Input.valueOf(KeyCode.F3.getCode()));

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
            // CLOSE
            case KEY_C -> {
                Map<Location, List<WorldElement>> neighborsWithSomethingOpen
                        = state.getWorld().getPlayerLocation().getNeighbors(true).stream()
                                .map(loc -> {
                                    // TODO: check for doors
                                    List<WorldElement> elements = state.getWorld().getAll(loc, Building.TypeNames.DOOR.name()).stream()
                                            .map(id -> {
                                                // I think having to use this syntax is a language
                                                // difficiency wrt generics.
                                                WorldElement el = Data.get(id.getId());
                                                return el;
                                            })
                                            .collect(Collectors.toList());

                                    if (elements.isEmpty()) {
                                        return null;
                                    } else {
                                        return Pair.create(loc, elements);
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(k -> k.getFirst(), v -> v.getSecond()));

                if (neighborsWithSomethingOpen.isEmpty()) {
                    copy.addLog("There is nothing to close.");
                } else {
                    if (neighborsWithSomethingOpen.size() == 1) {
                        Location loc = neighborsWithSomethingOpen.keySet().iterator().next();

                        // TODO Don't want an action here.. want to tell the
                        // screen to print the items at the location!!!
                        Action closeAction = new CloseDoorAction(loc);
                        if (closeAction.canExecute(state)) {
                            copy = closeAction.execute(state).copy();
                        } else {
                            // This should be very rare.. maybe a body in the way?
                            copy.addLog("Cannot close door");
                        }
                    } else {
                        // prompt for direction
                        // Note: This is the same "TODO" as elsewhere in this class
                        // TODO Don't want an action here.. want to tell the
                        // screen to print the items at the location!!!
                        copy.addInputContext(GameState.InputContext.builder()
                                .setName("CLOSE")
                                .setHandler(new ActionDirectionInputHandler(
                                        getGameConfiguration(),
                                        l -> new CloseDoorAction(l),
                                        state.getWorld().getPlayerLocation()))
                                .setPrompt(GameState.InputContextPrompt.DIRECTION)
                                .build()).build();
                    }
                }
            }
            // DROP - this goes away, ya?
            case KEY_D -> {
                // This is pretty clunky, eh?
                List<Item> items = state.getWorld().getPlayer().getInventory().getItems();

                if (items.isEmpty()) {
                    copy.addLog("There is nothing to drop.");
                } else if (items.size() == 1) {
                    Action getItem = new DropPlayerItemAction(items.get(0), false);
                    if (getItem.canExecute(state)) {
                        copy = getItem.execute(state).copy();
                    }
                } else {
                    copy.addInputContext(GameState.InputContext.builder()
                                    .setName("DROP")
                                    .setHandler(ListActionInputHandler.create(getGameConfiguration(),
                                            items, e -> new DropPlayerItemAction(e, false)))
                                    .setPrompt(GameState.InputContextPrompt.LIST)
                                    .build());
                }
            }
            // EXAMINE
            case KEY_E -> {
                Map<Location, List<Identifable>> neighborsWithSomething
                        = state.getWorld().getPlayerLocation().getNeighbors(true).stream()
                                .map(loc -> {
                                    List<Identifable> elements = state.getWorld().getAllElements(loc).stream()
                                            .filter(el -> !el.getId().equals("player"))
                                            .collect(Collectors.toList());

                                    if (elements.isEmpty()) {
                                        return null;
                                    } else {
                                        return Pair.create(loc, elements);
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(k -> k.getFirst(), v -> v.getSecond()));

                if (neighborsWithSomething.isEmpty()) {
                    copy.addLog("There is nothing to examine.");
                } else {
                    if (neighborsWithSomething.size() == 1) {
                        Location loc = neighborsWithSomething.keySet().iterator().next();

                        // TODO Don't want an action here.. want to tell the
                        // screen to print the items at the location!!!
                        Action examineAction = new ExamineAction(loc);
                        if (examineAction.canExecute(state)) {
                            copy = examineAction.execute(state).copy();
                        }
                    } else {
                        // prompt for direction
                        // TODO Don't want an action here.. want to tell the
                        // screen to print the items at the location!!!
                        copy.addInputContext(GameState.InputContext.builder()
                                .setName("EXAMINE")
                                .setHandler(new ActionDirectionInputHandler(
                                        getGameConfiguration(),
                                        l -> new ExamineAction(l),
                                        state.getWorld().getPlayerLocation()))
                                .setPrompt(GameState.InputContextPrompt.DIRECTION)
                                .build()).build();
                    }
                }
            }
            // CANCEL
            case ESCAPE -> {
                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("GAME_MENU")
                                .setHandler(new ModalInputHandler(getGameConfiguration()))
                                .setPrompt(GameState.InputContextPrompt.MODAL)
                                .build());
            }
            // GET
            case KEY_G -> {
                // This is pretty clunky, eh?
                List<Item> items = state.getWorld()
                        .getAll(state.getWorld().getPlayerLocation(), null).stream()
                        .filter(t -> state.getWorld().canGet(t.getTemplate()))
                        .filter(el -> {
                            return Item.class.isAssignableFrom(el.getClass());
                        })
                        .map(el -> (Item) el)
                        .collect(Collectors.toList());

                if (items.isEmpty()) {
                    copy.addLog("There is nothing to get.");
                } else if (items.size() == 1) {
                    Action getItem = new GetItemAction(items.get(0));
                    if (getItem.canExecute(state)) {
                        copy = getItem.execute(state).copy();
                    }
                } else {
                    copy.addInputContext(
                            GameState.InputContext.builder()
                                    .setName("GET")
                                    .setHandler(ListActionInputHandler.create(getGameConfiguration(),
                                            items, e -> new GetItemAction(e)))
                                    .setPrompt(GameState.InputContextPrompt.LIST)
                                    .build());
                }
            }
            // ABILITIES - present abilities
            case KEY_A -> {
                CharacterSheet character = state.getWorld().getPlayer();

                AbilityInputHandler abilityHandler = AbilityInputHandler.create(
                        getGameConfiguration(), character.getAbilities());

                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("ABILITY")
                                .setHandler(abilityHandler)
                                .setPrompt(GameState.InputContextPrompt.ABILITIES)
                                .build());
            }
            // INVENTORY
            case KEY_I -> {
                CharacterSheet player = state.getWorld().getPlayer();

                // This type of "pass me in" is going to happen at least a few times..
                InventoryInputHandler inventoryHandler = InventoryInputHandler.create(
                        getGameConfiguration(), player.getEquipment(), player.getInventory());

                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("INVENTORY")
                                .setHandler(inventoryHandler)
                                .setPrompt(GameState.InputContextPrompt.INVENTORY)
                                .build());
            }
            // MAP
            case KEY_M -> {
                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("MAP")
                                .setHandler(ContinueInputHandler.create(getGameConfiguration()))
                                .setPrompt(GameState.InputContextPrompt.MAP)
                                .build());
            }
            // PLAYER
            case KEY_P -> {
                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("PLAYER")
                                .setHandler(ContinueInputHandler.create(getGameConfiguration()))
                                .setPrompt(GameState.InputContextPrompt.PLAYER)
                                .build());
            }
            // TALK / could be E?
            case KEY_T -> {
                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("DIALOG")
                                .setHandler(new AbilityDirectionInputHandler(
                                        getGameConfiguration(), new InitiateDialog(getGameConfiguration()),
                                        state.getWorld().getPlayerLocation()) {

                                    // A couple of tweaks to AbilityDirectionInputHandler:
                                    //
                                    // Override the default invalid which pops several
                                    // layers, just pop the AbilityDirectionInputHandler instead.
                                    @Override
                                    public GameState handleInvalidInput(
                                            GameState state) {
                                        return state.copy()
                                                .removeInputContext()// ability target
                                                .removeInputContext()// ability target
                                                .build();
                                    }

                                    // Don't pop the state for dialog, let the dialog do it.
                                    @Override
                                    protected GameState popInputContext(
                                            GameState state) {
                                        return state;
                                    }
                                })
                                .setPrompt(GameState.InputContextPrompt.DIRECTION)
                                .build());
            }
            // WAIT
            case NUMPAD_5, DIGIT_5 -> {
                // TODO: wait action
                copy.addLog("Waiting");
                copy.setWorld(state.getWorld().addTime(DEFAULT_ACTION_TIME));
                break;
            }
            // MOVE/BUMP
            case NUMPAD_1, NUMPAD_2, NUMPAD_3, NUMPAD_4, /* NOT 5*/ NUMPAD_6, NUMPAD_7, NUMPAD_8, NUMPAD_9, DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4, /* NOT 5*/ DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9 -> {

                final World world = state.getWorld();
                CharacterSheet player = world.getPlayer();
                Location playerLocation = world.getPlayerLocation();

                Location destination = getRelativeLocation(playerLocation, input);
                if (state.getWorld().canMove(destination)) {
                    MoveAction moveAction = new MoveAction(player.getId(), playerLocation, destination);
                    if (moveAction.canExecute(state)) {
                        moveAction.execute(state);
                    }
                } else {
                    // Bump to interact
                    // First check for monsters
                    CharacterSheet interactable = state.getWorld().get(destination);

                    // TODO: hostile status
                    if (interactable != null) {
                        Action bumpAttack = new MeleeAttackAction(
                                getGameConfiguration(), player, playerLocation, interactable, destination);

                        GameState tmpState = copy.build();
                        if (bumpAttack.canExecute(tmpState)) {
                            copy = bumpAttack.execute(tmpState).copy();
                        }

                        // TODO: resolve damages, kill NPCs or player
                    } else {
                        // Next check on items in the way (ie doors)
                        GameState gs = copy.build();
                        List<Tangible> items = gs.getWorld().getAll(destination, null);

                        for (Identifable item : items) {
                            WorldElement element = Data.get(item.getId());

                            Boolean closed = element.getFlag(CommonAttributes.CLOSED.name());
                            if (closed != null && closed.booleanValue()) {
                                Action open = new OpenDoorAction(destination);
                                if (open.canExecute(gs)) {
                                    copy = open.execute(gs).copy();
                                    SoundEffectAction playEffect = new SoundEffectAction(getGameConfiguration(), "door_open");
                                    playEffect.execute(state);
                                    // Only open one thing..
                                    break;
                                }
                            }
                        }
                    }
                }

                // Same movement cost everywhere for now..
                // Same movement speed, etc
                copy.setWorld(state.getWorld().addTime(DEFAULT_ACTION_TIME));
            }
            case F1 -> {
                CharacterSheet player = state.getWorld().getPlayer();

                Field field = new Field(FieldData.get("fd_poison_gas"), Field.PropagationDirection.TARGET,
                        Field.PropagationStyle.STRAIGHT, Field.PropagationPotency.STRONG,
                        state.getWorld().getPlayerLocation(), 50);

                Set<Action> n = state.getWorld().getPlayerLocation().getNeighbors(4).stream()
                        .map(l -> new SetTangibleAction(l, field))
                        .collect(Collectors.toSet());

                GameState newState = state;
                for (Action a : n) {
                    if (a.canExecute(newState)) {
                        newState = a.execute(newState);
                    }
                }

                copy = newState.copy();
            }
            case F2 -> {
                CharacterSheet player = state.getWorld().getPlayer();

                Field field = new Field(FieldData.get("fd_poison_gas"), Field.PropagationDirection.EMIT,
                        Field.PropagationStyle.STRAIGHT, Field.PropagationPotency.STRONG,
                        state.getWorld().getPlayerLocation(), 20);

                Set<Action> n = state.getWorld().getPlayerLocation().getNeighbors(1).stream()
                        .map(l -> new SetTangibleAction(l, field))
                        .collect(Collectors.toSet());

                GameState newState = state;
                for (Action a : n) {
                    if (a.canExecute(newState)) {
                        newState = a.execute(newState);
                    }
                }

                copy = newState.copy();
            }
            case F3 -> {
                Field field = new Field(FieldData.get("fd_electricity"), Field.PropagationDirection.EMIT,
                        Field.PropagationStyle.STRAIGHT, Field.PropagationPotency.WEAK,
                        state.getWorld().getPlayerLocation(), 80);

                Location playerLocation = state.getWorld().getPlayerLocation();
                Function<Location, Action> getFieldSetFn = (l) -> new SetTangibleAction(l, field);
                Map<Input, Function<Location, Action>> examine = Map.of(
                        Input.valueOf(KeyCode.ENTER.getCode()),
                        getFieldSetFn,
                        Input.valueOf(KeyCode.NUMPAD_5.getCode()),
                        getFieldSetFn);

                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("TARGET")
                                .setHandler(new TargetingInputHandler(
                                        getGameConfiguration(), playerLocation, null, examine, true))
                                .setPrompt(GameState.InputContextPrompt.DIRECTION)
                                .build());
            }
            case KEY_Q -> {
                // TODO: A QuestInputHandler so the player can select a quest
                // and see more info about it..

                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("QUESTS")
                                .setHandler(ContinueInputHandler.create(getGameConfiguration()))
                                .setPrompt(GameState.InputContextPrompt.QUESTS)
                                .build());
            }
            case KEY_X -> {
                Location playerLocation = state.getWorld().getPlayerLocation();
                Function<Location, Action> getExFn = (l) -> new ExamineAction(l);
                Map<Input, Function<Location, Action>> examine = Map.of(
                        Input.valueOf(KeyCode.ENTER.getCode()),
                        getExFn,
                        Input.valueOf(KeyCode.NUMPAD_5.getCode()),
                        getExFn);

                copy.addInputContext(
                        GameState.InputContext.builder()
                                .setName("TARGET")
                                .setHandler(new TargetingInputHandler(getGameConfiguration(), playerLocation, examine))
                                .setPrompt(GameState.InputContextPrompt.DIRECTION)
                                .build());
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
            case KEY_E -> {
                return "Examine";
            }
            case KEY_D -> {
                return "Drop";
            }
            case KEY_I -> {
                return "Inventory";
            }
            case KEY_G -> {
                return "Get";
            }
            case F1 -> {
                return "F1";
            }
            case F2 -> {
                return "F2";
            }
            case F3 -> {
                return "F3";
            }
            case KEY_X -> {
                return "Look around";
            }
            default -> {
                return inputKeyCode.name();
            }
        }
    }

}
