package com.zygon.rl.world;

import com.zygon.rl.data.ItemClass;

/**
 *
 * @author zygon
 */
public class Item extends Tangible {

    public Item(ItemClass template, int weight) {
        super(template, weight);
    }

    public Item(ItemClass template) {
        this(template, template.getWeight());
    }
}
