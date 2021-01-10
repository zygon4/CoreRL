package com.zygon.rl.world.combat;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Equipment;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.Weapon;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.util.Map;
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

        CharacterSheet attacker = new CharacterSheet(
                new Element("player", "player", "z", Color.PINK.toString(), "attacker", ""),
                new Stats(10, 10, 10, 10, 10, 10),
                new Status(20, 10, Map.of()),
                new Equipment(new Weapon(20, 2, dagger, 0)),
                Set.of(), Set.of());

        CharacterSheet defender = new CharacterSheet(
                new Element("player", "player", "z", Color.PINK.toString(), "defender", ""),
                new Stats(10, 10, 10, 10, 10, 10),
                new Status(20, 10, Map.of()),
                new Equipment(new Weapon(20, 2, dagger, 0)),
                Set.of(), Set.of());

        CombatResolver.Resolution resolvedMelee = resolver.resolveCloseCombat(attacker, defender);

        Assert.assertNotNull(resolvedMelee);
        // TODO: expect simple result, no weapons/armor
    }

}
