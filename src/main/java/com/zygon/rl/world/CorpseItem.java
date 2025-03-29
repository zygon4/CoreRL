package com.zygon.rl.world;

import com.zygon.rl.data.items.Corpse;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 */
public class CorpseItem extends Item {

    private final String name;

    public CorpseItem(Corpse corpse, CharacterSheet characterSheet) {
        super(corpse, characterSheet.getWeight());
        this.name = createName(corpse, characterSheet);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static CorpseItem create(CharacterSheet characterSheet) {
        return new CorpseItem(Corpse.get(Corpse.Ids.CORPSE.getId()), characterSheet);
    }

    public static CorpseItem createMutilated(CharacterSheet characterSheet) {
        return new CorpseItem(Corpse.get(Corpse.Ids.CORPSE_MUT.getId()), characterSheet);
    }

    private static String createName(Corpse corpse,
            CharacterSheet characterSheet) {
        return corpse.getName() + " of a " + characterSheet.getSpecies();
    }
}
