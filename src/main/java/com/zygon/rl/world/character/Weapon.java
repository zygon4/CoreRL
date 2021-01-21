package com.zygon.rl.world.character;

import com.zygon.rl.util.DiceRoller;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.world.Item;

/**
 *
 */
public class Weapon extends Item {

    // TODO: should be a more generic "weapon" template when there are more types.
    private final Melee melee;
    private final int critRange; //lowest number weapon will crit on
    private final int critMod; //critical modifier
    private final int initMod; //Initiative Modifer

    public Weapon(Melee melee, int critRange, int critMod, int initMod) {
        super(melee);
        this.critRange = critRange;
        this.critMod = critMod;
        this.melee = melee;
        this.initMod = initMod;
    }

    private int calculateHit(DiceRoller dice, int toDamage) {
        return calcDamage(dice, melee.getDice(), melee.getDamage()) + toDamage;
    }

    public int calculateHit(DiceRoller dice) {
        return calculateHit(dice, melee.getToDamage());
    }

    public int calculateCrit(DiceRoller dice) {
        int damageDealt = 0;

        for (int i = 0; i < critMod; i++) {
            damageDealt += calculateHit(dice, 0);
        }

        return damageDealt + melee.getToDamage();
    }

    public int getCritRange() {
        return critRange;
    }

    public int getInitMod() {
        return initMod;
    }

    public int getToHit() {
        return melee.getToDamage();
    }

    private static int calcDamage(DiceRoller dice, int numDice, int damage) {
        int damageDealt = 0;
        for (int j = 0; j < numDice; j++) {
            damageDealt += dice.roll(damage);
        }
        return damageDealt;
    }
}
