package com.zygon.rl.world;

import com.zygon.rl.data.ItemClass;

/**
 *
 * @author zygon
 */
public class Item {

    private final ItemClass template;

    public Item(ItemClass template) {
        this.template = template;
    }

    public ItemClass getTemplate() {
        return template;
    }

    public String getDescription() {
        return template.getDescription();
    }

    public String getName() {
        return template.getName();
    }
}
