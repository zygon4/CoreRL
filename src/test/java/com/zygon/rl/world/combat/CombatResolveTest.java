package com.zygon.rl.world.combat;

import com.zygon.rl.data.Creature;
import com.zygon.rl.data.Element;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.data.monster.Species;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.Weapon;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author zygon
 */
public class CombatResolveTest {

    private final Random random = new Random();

    @Test
    public void testBasicMelee() {
        CombatResolver resolver = new CombatResolver(random);

        Data.load();
        Melee dagger = Melee.get("dagger");
        Weapon weapon = new Weapon(dagger, 20, 2, 0);

        Element eleATemplate = new Element("player", "player", "z", Color.PINK.toString(), "attacker", "");
        CharacterSheet attacker = new CharacterSheet(
                new Creature(eleATemplate, Species.MAMMAL.name(), 100, 120, 100, Set.of()),
                new Stats(10, 10, 10, 10, 10, 10),
                new Status(20, 10, Set.of()),
                null,
                null,
                Set.of(), Set.of());

        attacker = attacker.add(weapon).wield(weapon);

        Element eleDTemplate = new Element("player", "player", "z", Color.PINK.toString(), "defender", "");
        CharacterSheet defender = new CharacterSheet(
                new Creature(eleDTemplate, Species.MAMMAL.name(), 100, 120, 100, Set.of()),
                new Stats(10, 10, 10, 10, 10, 10),
                new Status(20, 10, Set.of()),
                null,
                null,
                Set.of(), Set.of());

        defender = defender.add(weapon).wield(weapon);

        DamageResolution resolvedMelee = resolver.resolveCloseCombat(attacker, defender);

        Assert.assertNotNull(resolvedMelee);
        // TODO: expect simple result, no weapons/armor
    }

}
