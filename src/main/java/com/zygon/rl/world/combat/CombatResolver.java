package com.zygon.rl.world.combat;

import com.zygon.rl.world.DamageType;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.DiceRoller;
import com.zygon.rl.world.character.Weapon;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class CombatResolver {

    public static class Resolution {

        private final String attacker;
        private final String defender;
        private final boolean miss;
        private final boolean critial;
        private final Map<DamageType, Integer> damageByType = new LinkedHashMap<>();
        private int totalDamage = 0;

        // TODO: damage to weapons/armor/items on person, or even damage
        // to the local area (acid spray, etc.)
        //
        public Resolution(String attacker, String defender, boolean miss, boolean critial) {
            this.attacker = attacker;
            this.defender = defender;
            this.miss = miss;
            this.critial = critial;
            if (miss && critial) {
                throw new IllegalArgumentException();
            }
        }

        // Will override, not add
        private void set(DamageType damage, int ammount) {
            damageByType.put(damage, ammount);
            totalDamage += ammount;
        }

        public Map<DamageType, Integer> getDamageByType() {
            return Collections.unmodifiableMap(damageByType);
        }

        public int getTotalDamage() {
            return totalDamage;
        }

        public boolean isCritial() {
            return critial;
        }

        public boolean isMiss() {
            return miss;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            if (miss) {
                sb.append(attacker).append(" missed!\n");
            } else {
                if (critial) {
                    sb.append("Critical!\n");
                }

                sb.append(attacker).append(" hit ").append(defender)
                        .append(" for ").append(getTotalDamage()).append(" damage\n  ")
                        .append(damageByType.entrySet().stream()
                                .map(entry -> entry.getKey().name() + " - " + entry.getValue() + " damage")
                                .collect(Collectors.joining("\n  ")));
            }
            return sb.toString();
        }
    }

    private final Random random;
    private final DiceRoller dice;

    public CombatResolver(Random random) {
        this.random = random;
        this.dice = new DiceRoller(this.random);
    }

    public CombatResolver.Resolution resolveCloseCombat(CharacterSheet attacker, CharacterSheet defender) {
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

    private CombatResolver.Resolution resolveMelee(CharacterSheet attacker, CharacterSheet defender) {

        Weapon weapon = attacker.getEquipment().getEquippedRightWeapon();
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
                    : 1 + attacker.getStats().getStrength();
        } else {
            if (!fail) {
                // regular hit
                int toHitModifier = attackRoll + attacker.getStats().getStrength();
                if (weapon != null) {
                    toHitModifier += weapon.getToHit();
                }
                if (toHitModifier > defenderAC) {
                    miss = false;
                    dmg = weapon != null
                            ? weapon.calculateHit(dice)
                            : 1 + attacker.getStats().getStrength();
                }
            }
        }

        Resolution resolution = new Resolution(attacker.getName(), defender.getName(), miss, critical);
        if (dmg > 0) {
            // TODO: different weapon types
            resolution.set(DamageType.Bludgeoning, dmg);
        }

        return resolution;
    }

}
