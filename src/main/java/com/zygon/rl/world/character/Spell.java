package com.zygon.rl.world.character;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.Identifable;

/**
 *
 * @author zygon
 */
public class Spell implements Identifable {

    private final Element template;

    public Spell(Element template) {
        this.template = template;
    }

    @Override
    public String getId() {
        return template.getId();
    }

}
