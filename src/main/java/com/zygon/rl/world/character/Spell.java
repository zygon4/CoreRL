package com.zygon.rl.world.character;

import com.zygon.rl.data.WorldElement;
import com.zygon.rl.data.Identifable;

/**
 *
 * @author zygon
 */
public class Spell implements Identifable {

    private final WorldElement template;

    public Spell(WorldElement template) {
        this.template = template;
    }

    @Override
    public String getId() {
        return template.getId();
    }

}
