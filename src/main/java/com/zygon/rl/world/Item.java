package com.zygon.rl.world;

import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.ItemClass;

/**
 *
 * @author zygon
 */
public class Item implements Identifable {

    private final ItemClass template;

    public Item(ItemClass template) {
        this.template = template;
    }

    public ItemClass getTemplate() {
        return template;
    }

    @Override
    public String getId() {
        return template.getId();
    }

    public String getDescription() {
        return template.getDescription();
    }

    public String getName() {
        return template.getName();
    }

    @Override
    public String toString() {
        return getName() + " - " + getDescription();
    }
}
