package com.zygon.rl.world.character;

import com.zygon.rl.world.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: capacity
 *
 * @author zygon
 */
public final class Inventory {

    private final List<Item> items;

    private Inventory(List<Item> items) {
        this.items = items != null
                ? Collections.unmodifiableList(items) : Collections.emptyList();
    }

    public Inventory() {
        this(null);
    }

    public Inventory add(Item item) {
        List<Item> updatedItems = new ArrayList<>(items);

        updatedItems.add(item);

        return new Inventory(updatedItems);
    }

    // TODO: later
    public boolean canAdd(Item item) {
        return true;
    }

    public Item getItem(String id) {
        return getItems().stream()
                .filter(i -> i.getTemplate().getId().equals(id))
                .findFirst().orElse(null);
    }

    public List<Item> getItems() {
        return items;
    }

    public Inventory remove(Item item) {
        List<Item> updatedItems = new ArrayList<>(items);

        Item removedItem = null;

        int itemIndex;
        for (itemIndex = 0; itemIndex < items.size(); itemIndex++) {
            if (items.get(itemIndex).getTemplate().getId().equals(item.getTemplate().getId())) {
                removedItem = updatedItems.remove(itemIndex);
                break;
            }
        }

        if (removedItem == null) {
            throw new IllegalArgumentException("Item not found: " + item.getTemplate().getId());
        }

        return new Inventory(updatedItems);
    }
}
