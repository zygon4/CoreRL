package com.zygon.rl.world.combat;

import java.util.Random;

import com.zygon.rl.util.DiceRoller;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.DamageType;
import com.zygon.rl.world.SourcedDamageResolution;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Weapon;

/**
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

    // TODO:
    private static int calculateAC(CharacterSheet character) {
        return 14;
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
        // TODO: second weapon!

        int attackRoll = dice.rollD20();
        boolean critical = isCritical(weapon, attackRoll);
        boolean fail = isFail(attackRoll);
        boolean miss = fail;
        int defenderAC = calculateAC(defender);
        int dmg = 0;

        if (critical) {
            dmg = weapon != null
                    ? weapon.calculateCrit(dice)
                    : 1 + attacker.getModifiedStats().getStrength();
        } else {
            if (!fail) {
                // regular hit
                int toHitModifier = attackRoll + attacker.getModifiedStats().getStrength();
                if (weapon != null) {
                    toHitModifier += weapon.getToHit();
                }
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
