package com.zygon.rl.game;

import com.zygon.rl.game.GameState.InputContext;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.character.Inventory;
import org.hexworks.zircon.api.uievent.KeyCode;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

// We need a "list" handler which dynamically adds the controls to show
// more (scroll up, down).
final class InventoryInputHandler extends BaseInputHandler {

    private final InputContext escapeContext;
    private final Map<Input, Item> itemsByKeyCode;

    private static Set<Input> getInputs(Map<Input, Item> itemsByKeyCode) {
        Set<Input> inputs = new LinkedHashSet<>(itemsByKeyCode.keySet());

        inputs.add(Input.valueOf(KeyCode.ESCAPE.getCode()));
        // TODO: more? also see above for the "list" handling

        return inputs;
    }

    // TODO: for these top-level handlers - they should be on the stack alone without the "Default" handlers under them
    // But that means here, when the player mashes escape, or we want to go back to the game, we need to pop us, and add
    // back a context.
    private InventoryInputHandler(GameConfiguration gameConfiguration,
            Map<Input, Item> itemsByKeyCode, InputContext escapeContext) {
        super(gameConfiguration, getInputs(itemsByKeyCode));
        this.itemsByKeyCode = itemsByKeyCode;
        this.escapeContext = escapeContext;
    }

    public static final InventoryInputHandler create(GameConfiguration gameConfiguration,
            Inventory inventory, InputContext escapeContext) {
        Map<Input, Item> inputs = createAlphaInputs(inventory.getItems());
        return new InventoryInputHandler(gameConfiguration, inputs, escapeContext);
    }

    @Override
    public GameState apply(final GameState state, Input input) {

        GameState newState = state;

        switch (convert(input)) {
            case ESCAPE -> {
                newState = newState.copy()
                        .removeInputContext()
                        .addInputContext(escapeContext)
                        .build();
            }
            default -> {
                Item item = itemsByKeyCode.get(input);
                System.out.println("ITEM: " + item.getName() + ") " + item.getDescription());
            }
        }

        return newState;
    }

    @Override
    public String getDisplayText(Input input) {
        Item item = itemsByKeyCode.get(input);
        return item.getName();
    }

    /*pkg*/ Map<Input, Item> getItemByKeyCode() {
        return itemsByKeyCode;
    }
}
