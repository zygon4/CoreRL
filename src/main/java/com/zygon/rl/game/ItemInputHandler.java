/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static com.zygon.rl.game.BaseInputHandler.convert;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.action.DropPlayerItemAction;
import com.zygon.rl.world.action.RemovePlayerItemAction;
import com.zygon.rl.world.action.WearPlayerItemAction;
import com.zygon.rl.world.action.WieldPlayerItemAction;
import com.zygon.rl.world.character.Armor;
import com.zygon.rl.world.character.Weapon;

import org.hexworks.zircon.api.uievent.KeyCode;
import static org.hexworks.zircon.api.uievent.KeyCode.ESCAPE;
import static org.hexworks.zircon.api.uievent.KeyCode.KEY_A;
import static org.hexworks.zircon.api.uievent.KeyCode.KEY_D;
import static org.hexworks.zircon.api.uievent.KeyCode.KEY_W;

/**
 * A specific item. Items need to expose behaviors i.e. "can drop", "can wield",
 * etc.
 *
 * @author zygon
 */
public final class ItemInputHandler extends BaseInputHandler {

    public static interface ItemHandler {

        String getName();

        String getDescription();

        boolean canDrop();

        boolean canWear();

        boolean canWield();

        // stop wielding/wearing
        boolean canRemove();
    }

    private final Item item;
    private final boolean equipped;

    private ItemInputHandler(GameConfiguration gameConfiguration, Item item,
            boolean equipped) {
        super(gameConfiguration, getInputs(create(item, equipped)));
        this.item = item;
        this.equipped = equipped;
    }

    public static final ItemInputHandler create(
            GameConfiguration gameConfiguration, Item item, boolean equipped) {
        return new ItemInputHandler(gameConfiguration, item, equipped);
    }

    @Override
    public GameState apply(final GameState state, Input input) {

        GameState newState = state;

        switch (convert(input)) {
            case ESCAPE -> {
                newState = dropInputContext(newState);
            }
            case KEY_D -> {
                DropPlayerItemAction drop = new DropPlayerItemAction(getItem(), isEquipped());
                if (drop.canExecute(newState)) {
                    newState = drop.execute(newState);
                    // Here we drop the inventory context completely. It would be
                    // a better user experience to drop directly to the inventory
                    // context but it has old data now.
                    newState = dropInputContext(newState, 2);
                }
            }
            case KEY_A -> {
                Armor armor = (Armor) getItem();
                WearPlayerItemAction wear = new WearPlayerItemAction(armor);
                if (wear.canExecute(newState)) {
                    newState = wear.execute(newState);
                    // Same comment as above
                    newState = dropInputContext(newState, 2);
                }
            }
            case KEY_R -> {
                RemovePlayerItemAction remove = new RemovePlayerItemAction(getItem());
                if (remove.canExecute(newState)) {
                    newState = remove.execute(state);
                    // Same comment as above
                    newState = dropInputContext(newState, 2);
                }
            }
            case KEY_W -> {
                Weapon weapon = (Weapon) getItem();
                WieldPlayerItemAction wield = new WieldPlayerItemAction(weapon);
                if (wield.canExecute(newState)) {
                    newState = wield.execute(newState);
                    // Same comment as above
                    newState = dropInputContext(newState, 2);
                }
            }
            default -> {
                System.out.println("ITEM: ???");
            }
        }

        return newState;
    }

    @Override
    public String getDisplayText(Input input) {

        switch (convert(input)) {
            case KEY_D -> {
                return "Drop";
            }
            case KEY_A -> {
                return "Wear";
            }
            case KEY_R -> {
                return "Remove";
            }
            case KEY_W -> {
                return "Wield";
            }
            default -> {
                throw new IllegalStateException("bug?");
            }
        }
    }

    public static BiFunction<GameConfiguration, GameState, List<String>> getInputsFn() {
        return (cfg, gameState) -> {
            GameState.InputContext ic = gameState.getInputContext().peek();
            if (ic.getPrompt() == GameState.InputContextPrompt.ITEM) {
                LayerInputHandler handler = ic.getHandler();

                // Casting is usually a hack.. this is no exception..
                ItemInputHandler inputHandler = (ItemInputHandler) handler;

                List<String> itemText = new ArrayList<>();

                final Item item = inputHandler.getItem();
                item.toDisplay(itemText);
                itemText.add("\n");
                addText(itemText, inputHandler.getInputs(), inputHandler);
                return itemText;
            }
            return null;
        };
    }

    private static void addText(List<String> destText, Set<Input> inputs,
            ItemInputHandler handler) {
        inputs.stream()
                .filter(input -> convert(input) != KeyCode.ESCAPE) // filter out implied escape character
                .map(input -> input + ") " + handler.getDisplayText(input))
                .forEach(destText::add);
    }

    // This will soon need a game state
    private static ItemHandler create(Item item, boolean equipped) {

        return new ItemHandler() {
            @Override
            public String getName() {
                return item.getName();
            }

            @Override
            public String getDescription() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean canDrop() {
                // TODO: if not cursed, etc.
                return Boolean.TRUE;
            }

            @Override
            public boolean canWear() {
                return !equipped && Armor.class.isAssignableFrom(item.getClass());
            }

            @Override
            public boolean canWield() {
                return !equipped && Weapon.class.isAssignableFrom(item.getClass());
            }

            @Override
            public boolean canRemove() {
                return equipped;
            }
        };
    }

    private Item getItem() {
        return item;
    }

    public boolean isEquipped() {
        return equipped;
    }

    private static Set<Input> getInputs(ItemHandler itemHandler) {
        Set<Input> inputs = new LinkedHashSet<>();
        inputs.add(Input.valueOf(KeyCode.ESCAPE.getCode()));

        if (itemHandler.canDrop()) {
            inputs.add(Input.valueOf(KeyCode.KEY_D.getCode()));
        }

        if (itemHandler.canWear()) {
            // "a" feels weird for wear..
            inputs.add(Input.valueOf(KeyCode.KEY_A.getCode()));
        }

        if (itemHandler.canRemove()) {
            inputs.add(Input.valueOf(KeyCode.KEY_R.getCode()));
        }

        if (itemHandler.canWield()) {
            inputs.add(Input.valueOf(KeyCode.KEY_W.getCode()));
        }

        return inputs;
    }
}
