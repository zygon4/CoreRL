package com.zygon.rl.world.character;

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

    public Inventory remove(Item item) {
        List<Item> updatedItems = new ArrayList<>(items);

        Item removedItem = null;
        int itemIndex;
        for (itemIndex = 0; itemIndex < items.size(); itemIndex++) {
            if (items.get(itemIndex).getTemplate().getId().equals(item.getTemplate().getId())) {
                removedItem = items.remove(itemIndex);
                break;
            }
        }

        if (removedItem != null) {
            updatedItems.remove(itemIndex);
        } else {
            throw new IllegalArgumentException("Item not found: " + item.getTemplate().getId());
        }

        return new Inventory(updatedItems);
    }

    public List<Item> getItems() {
        return items;
    }
}
