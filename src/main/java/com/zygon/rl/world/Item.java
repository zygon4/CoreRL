package com.zygon.rl.world;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.ItemClass;

/**
 *
 * @author zygon
 */
public class Item implements Identifable {

    private final ItemClass item;

    public Item(ItemClass template) {
        this.item = template;
    }

    public ItemClass getTemplate() {
        return item;
    }

    @Override
    public String getId() {
        return item.getId();
    }

    public String getDescription() {
        return item.getDescription();
    }

    public String getName() {
        return item.getName();
    }

    @Override
    public String toString() {
        return getName() + " - " + getDescription();
    }
}
