package com.zygon.rl.world.character;

import com.zygon.rl.world.Item;

/**
 *
 */
public class Armor extends Item {

    private final com.zygon.rl.data.items.Armor template;

    public Armor(com.zygon.rl.data.items.Armor template) {
        super(template);

        this.template = template;
    }

    @Override
    public com.zygon.rl.data.items.Armor getTemplate() {
        return template;
    }
}
