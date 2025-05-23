package com.zygon.rl.world.combat;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.zygon.rl.data.Creature;
import com.zygon.rl.data.PoolData;
import com.zygon.rl.data.WorldElement;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.data.monster.Species;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Pool;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.Weapon;

import org.junit.Assert;
import org.junit.Test;

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

        WorldElement eleATemplate = new WorldElement("player", "player", "z",
                Color.PINK.toString(), "attacker", "", Map.of(), 150);

        Creature creature = new Creature(eleATemplate, Species.MAMMAL.name(), 100,
                List.of(PoolData.Pools.HEALTH_SMALL.getId()), 100);
        Set<Pool> pools = creature.getPools().stream()
                .map(id -> PoolData.get(id))
                .map(e -> Pool.createMax(e))
                .collect(Collectors.toSet());
        CharacterSheet attacker = CharacterSheet.create(
                creature,
                "attacker",
                new Stats(10, 10, 10, 10, 10, 10),
                new Status(20, pools, Set.of()))
                .build();

        attacker = attacker.add(weapon).wield(weapon);

        WorldElement eleDTemplate = new WorldElement("player", "player", "z",
                Color.PINK.toString(), "defender", "", Map.of(), 150);
        Creature creature2 = new Creature(eleDTemplate, Species.MAMMAL.name(), 100,
                List.of(PoolData.Pools.HEALTH_LARGE.getId()), 100);
        Set<Pool> pools2 = creature2.getPools().stream()
                .map(id -> PoolData.get(id))
                .map(e -> Pool.createMax(e))
                .collect(Collectors.toSet());
        CharacterSheet defender = CharacterSheet.create(
                creature2,
                "defender",
                new Stats(10, 10, 10, 10, 10, 10),
                new Status(20, pools2, Set.of()))
                .build();

        defender = defender.add(weapon).wield(weapon);

        DamageResolution resolvedMelee = resolver.resolveCloseCombat(attacker, defender);

        Assert.assertNotNull(resolvedMelee);
        System.out.println(resolvedMelee);
        // TODO: expect simple result, no weapons/armor
    }

}
