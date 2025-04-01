package com.zygon.rl.world.combat;

import java.util.Random;

import com.zygon.rl.data.character.Proficiencies;
import com.zygon.rl.util.DiceRoller;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.DamageType;
import com.zygon.rl.world.SourcedDamageResolution;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Proficiency;
import com.zygon.rl.world.character.Weapon;

/**
 * TODO: Full features (armor, etc).
 *
 * @author zygon
 */
public class CombatResolver {

    private final Random random;
    private final DiceRoller dice;

    public CombatResolver(Random random) {
        this.random = random;
        this.dice = new DiceRoller(this.random);
    }

    public DamageResolution resolveCloseCombat(CharacterSheet attacker,
            CharacterSheet defender) {
        // TODO: check on weapon type and call specific method
        return resolveMelee(attacker, defender);
    }

    private static int calculateAC(CharacterSheet character) {
        return character.getAV();
    }

    private static boolean isCritical(Weapon weapon, int roll) {
        return weapon != null
                ? roll >= weapon.getCritRange()
                : roll == 20;
    }

    private static boolean isFail(int roll) {
        return roll == 1;
    }

    private DamageResolution resolveMelee(CharacterSheet attacker,
            CharacterSheet defender) {

        Weapon weapon = attacker.getEquipment().getWeapons().size() > 1
                ? attacker.getEquipment().getWeapons().get(0) : null;
        // TODO: second weapon/ranged weapons

        int attackRoll = dice.rollD20();
        boolean critical = isCritical(weapon, attackRoll);
        boolean fail = isFail(attackRoll);
        boolean miss = fail;
        int defenderAC = calculateAC(defender);
        int dmg = 0;

        if (critical) {
            // Is this missing the proficiency bonus?
            dmg = weapon != null
                    ? weapon.calculateCrit(dice)
                    : 1 + attacker.getModifiedStats().getStrength();
        } else {
            // regular hit, assumes melee for now
            if (!fail) {
                // Start with the roll
                int toHitModifier = attackRoll;

                // Add the strength modifier
                toHitModifier += attacker.getModifiedStats().getStrengthModifier();

                // Add melee proficiency if available
                Proficiency melee = attacker.getProficiency(Proficiencies.Names.MELEE.getId());
                if (melee != null) {
                    toHitModifier += melee.getPoints();
                }

                // Add weapon hit bonus if available
                if (weapon != null) {
                    toHitModifier += weapon.getToHit();
                }

                // Check if we hit
                if (toHitModifier > defenderAC) {
                    miss = false;
                    dmg = weapon != null
                            ? weapon.calculateHit(dice)
                            : 1 + attacker.getModifiedStats().getStrength();
                }
            }
        }

        DamageResolution resolution = new SourcedDamageResolution(attacker.getName(),
                defender.getName(), miss, critical);
        if (dmg > 0) {
            // TODO: different weapon types
            resolution.set(DamageType.Bludgeoning, dmg);
        }

        return resolution;
    }

}
