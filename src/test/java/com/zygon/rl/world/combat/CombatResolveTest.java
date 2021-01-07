package com.zygon.rl.world.combat;

import com.zygon.rl.world.DamageType;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Equipment;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.Weapon;
import org.junit.Assert;
import org.junit.Test;

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

        CharacterSheet attacker = new CharacterSheet(
                "attacker", "desc",
                new Stats(10, 10, 10, 10, 10, 10),
                new Status(20, 10, Map.of()),
                new Equipment(new Weapon(20, 2, 4, 1, 0, 0, 0)),
                Set.of(), Set.of());

        CharacterSheet defender = new CharacterSheet(
                "defender", "desc",
                new Stats(10, 10, 10, 10, 10, 10),
                new Status(20, 10, Map.of()),
                new Equipment(new Weapon(20, 2, 4, 1, 0, 0, 0)),
                Set.of(), Set.of());

        CombatResolver.Resolution resolvedMelee = resolver.resolveCloseCombat(attacker, defender);

        // TODO: expect simple result, no weapons/armor
        // bare handed
        Integer ammount = resolvedMelee.getDamageByType().get(DamageType.Bludgeoning);
        Assert.assertNotNull(ammount);
        Assert.assertTrue(ammount.intValue() > 0);
    }

}
