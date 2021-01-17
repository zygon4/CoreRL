package com.zygon.rl.world.character;

import com.zygon.rl.data.items.ArmorData;
import com.zygon.rl.world.Item;

/**
 *
 */
public class Armor extends Item {

    private final ArmorData template;

    public Armor(ArmorData template) {
        super(template);

        this.template = template;
    }

    @Override
    public ArmorData getTemplate() {
        return template;
    }
}
