package com.zygon.rl.world.character;

/**
 * Also adding items/weapons Melee, so these need to be reconciled.
 */
public class Weapon {

    private final int critRange; //lowest number weapon will crit on
    private final int critMod; //critical modifier
    private final int damage; //damage the weapon does if 1d4 would be 4
    private final int numDice; //number of dice rolled if 2d4 would be 2
    private final int toHit; //to hit modifier
    private final int toDamage; //plus to damage
    private final int initMod; //Initiative Modifer

    public Weapon(int critRange, int critMod, int damage, int numDice,
            int toHit, int toDamage, int initMod) {
        this.critRange = critRange;
        this.critMod = critMod;
        this.damage = damage;
        this.numDice = numDice;
        this.toHit = toHit;
        this.toDamage = toDamage;
        this.initMod = initMod;
    }

    private int calculateHit(DiceRoller dice, int toDamage) {
        return calcDamage(dice, numDice, damage) + toDamage;
    }

    public int calculateHit(DiceRoller dice) {
        return calculateHit(dice, toDamage);
    }

    public int calculateCrit(DiceRoller dice) {
        int damageDealt = 0;

        for (int i = 0; i < critMod; i++) {
            damageDealt += calculateHit(dice, 0);
        }

        return damageDealt + toDamage;
    }

    public int getCritRange() {
        return critRange;
    }

    public int getInitMod() {
        return initMod;
    }

    public int getToHit() {
        return toHit;
    }

    private static int calcDamage(DiceRoller dice, int numDice, int damage) {
        int damageDealt = 0;
        for (int j = 0; j < numDice; j++) {
            damageDealt += dice.roll(damage);
        }
        return damageDealt;
    }
}
