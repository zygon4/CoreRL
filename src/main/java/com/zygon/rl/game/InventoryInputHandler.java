/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.zygon.rl.game.ui.render.InventoryRenderer;
import com.zygon.rl.util.StringUtil;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.character.Equipment;
import com.zygon.rl.world.character.Inventory;

import org.hexworks.zircon.api.uievent.KeyCode;

// We need a "list" handler which dynamically adds the controls to show
// more (scroll up, down).
public final class InventoryInputHandler extends BaseInputHandler {

    private final Map<Input, Item> equipmentByKeyCode;
    private final Map<Input, Item> invByKeyCode;

    private static Set<Input> getInputs(Map<Input, Item> itemsByKeyCode) {
        Set<Input> inputs = new LinkedHashSet<>(itemsByKeyCode.keySet());
        inputs.add(Input.valueOf(KeyCode.ESCAPE.getCode()));
        return inputs;
    }

    private static Map<Input, Item> combine(Map<Input, Item> equip,
            Map<Input, Item> inv) {
        Map<Input, Item> itemsByKeyCode = new LinkedHashMap<>();
        itemsByKeyCode.putAll(equip);
        itemsByKeyCode.putAll(inv);
        return itemsByKeyCode;
    }

    // TODO: for these top-level handlers - they should be on the stack alone without the "Default" handlers under them
    // But that means here, when the player mashes escape, or we want to go back to the game, we need to pop us, and add
    // back a context.
    private InventoryInputHandler(GameConfiguration gameConfiguration,
            Map<Input, Item> equipmentByKeyCode, Map<Input, Item> invByKeyCode) {
        super(gameConfiguration, getInputs(combine(equipmentByKeyCode, invByKeyCode)));
        this.equipmentByKeyCode = equipmentByKeyCode;
        this.invByKeyCode = invByKeyCode;
    }

    public static final InventoryInputHandler create(
            GameConfiguration gameConfiguration, Equipment equipment,
            Inventory inventory) {

        List<Item> equip = new ArrayList<>();
        equipment.getEquipmentBySlot().forEach((s, ls) -> {
            equip.addAll(ls.stream().collect(Collectors.toList()));
        });
        Map<Input, Item> equipmentByKeyCode = createAlphaInputs(equip);

        Map<Input, Item> invByKeyCode = createAlphaInputs(
                inventory.getItems(), equipmentByKeyCode.size());

        return new InventoryInputHandler(gameConfiguration, equipmentByKeyCode, invByKeyCode);
    }

    @Override
    public GameState apply(final GameState state, Input input) {

        GameState newState = state;

        switch (convert(input)) {
            case ESCAPE -> {
                newState = newState.copy()
                        .removeInputContext()
                        .build();
            }
            default -> {
                boolean equipped = true;
                Item item = equipmentByKeyCode.get(input);
                if (item == null) {
                    item = invByKeyCode.get(input);
                    equipped = false;
                }

                if (item == null) {
                    throw new IllegalStateException("Item not found..");
                }

                newState = newState.copy()
                        .addInputContext(
                                GameState.InputContext.builder()
                                        .setName(GameState.InputContextPrompt.ITEM.name())
                                        .setHandler(ItemInputHandler.create(getGameConfiguration(), item, equipped))
                                        .setPrompt(GameState.InputContextPrompt.ITEM)
                                        .build())
                        .build();
            }
        }

        return newState;
    }

    @Override
    public String getDisplayText(Input input) {

        StringBuilder sb = new StringBuilder();

        Item item = get(input);

        final String name = StringUtil.padEnd(item.getName(), '.', InventoryRenderer.INV_OFFSET - 10);
        sb.append(name);
        sb.append(item.getWeight());
        sb.append(" ").append(getGameConfiguration().getWeightUnit());

        return sb.toString();
    }

    public static Function<GameState, Map<Boolean, List<String>>> getInputsFn() {
        return gameState -> {
            GameState.InputContext ic = gameState.getInputContext().peek();
            if (ic.getPrompt() == GameState.InputContextPrompt.INVENTORY) {
                LayerInputHandler handler = ic.getHandler();

                // Casting is usually a hack.. this is no exception..
                InventoryInputHandler invHandler = (InventoryInputHandler) handler;

                Map<Boolean, List<String>> invTextByEquipped = new HashMap<>();

                List<String> equippedText = invTextByEquipped.computeIfAbsent(Boolean.TRUE, b -> new ArrayList<>());
                addText(equippedText, invHandler.getEquipmentByKeyCode().keySet(), invHandler);

                List<String> invText = invTextByEquipped.computeIfAbsent(Boolean.FALSE, b -> new ArrayList<>());
                addText(invText, invHandler.getInvByKeyCode().keySet(), invHandler);
                return invTextByEquipped;
            }
            return null;
        };
    }

    private Map<Input, Item> getEquipmentByKeyCode() {
        return equipmentByKeyCode;
    }

    private Map<Input, Item> getInvByKeyCode() {
        return invByKeyCode;
    }

    private Item get(Input input) {
        Item item = equipmentByKeyCode.get(input);
        if (item == null) {
            item = invByKeyCode.get(input);
        }
        return item;
    }

    private static void addText(List<String> destText, Set<Input> inputs,
            InventoryInputHandler handler) {
        inputs.stream()
                .filter(input -> convert(input) != KeyCode.ESCAPE) // filter out implied escape character
                .map(input -> input + ") " + handler.getDisplayText(input))
                .forEach(destText::add);
    }
}
