package com.zygon.rl.world;

import com.zygon.rl.data.items.Corpse;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 */
public class CorpseItem extends Item {

    public CorpseItem(Corpse corpse, int weight) {
        super(corpse, weight);
    }

    public static CorpseItem create(CharacterSheet characterSheet) {
        return new CorpseItem(Corpse.get("corpse"), characterSheet.getWeight());
    }
}
