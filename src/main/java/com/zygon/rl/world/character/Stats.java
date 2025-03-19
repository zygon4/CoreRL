package com.zygon.rl.world.character;

import java.util.LinkedHashSet;
import java.util.Set;

import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.IntegerAttribute;

/**
 *
 * @author zygon
 */
public class Stats {

    private final int strength;
    private final int dexterity;
    private final int constitution;
    private final int intelligence;
    private final int wisdom;
    private final int charisma;

    public Stats() {
        this.strength = 0;
        this.dexterity = 0;
        this.constitution = 0;
        this.intelligence = 0;
        this.wisdom = 0;
        this.charisma = 0;
    }

    public Stats(int strength, int dexterity, int constitution,
            int intelligence, int wisdom, int charisma) {
        this.strength = strength;
        this.dexterity = dexterity;
        this.constitution = constitution;
        this.intelligence = intelligence;
        this.wisdom = wisdom;
        this.charisma = charisma;
    }

    public int getCharisma() {
        return charisma;
    }

    public int getConstitution() {
        return constitution;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getStrength() {
        return strength;
    }

    public int getWisdom() {
        return wisdom;
    }

    public Stats incStr(int ammount) {
        return new Stats(strength + ammount, dexterity, constitution, intelligence, wisdom, charisma);
    }

    public Stats incDex(int ammount) {
        return new Stats(strength, dexterity + ammount, constitution, intelligence, wisdom, charisma);
    }

    public Stats incCon(int ammount) {
        return new Stats(strength, dexterity, constitution + ammount, intelligence, wisdom, charisma);
    }

    public Stats incInt(int ammount) {
        return new Stats(strength, dexterity, constitution, intelligence + ammount, wisdom, charisma);
    }

    public Stats incWis(int ammount) {
        return new Stats(strength, dexterity, constitution, intelligence, wisdom + ammount, charisma);
    }

    public Stats incCha(int ammount) {
        return new Stats(strength, dexterity, constitution, intelligence, wisdom, charisma + ammount);
    }

    public Set<Attribute> getAttributes() {
        Set<Attribute> stats = new LinkedHashSet<>();

        stats.add(IntegerAttribute.create("STR", "Strength", strength));
        stats.add(IntegerAttribute.create("DEX", "Dexterity", dexterity));
        stats.add(IntegerAttribute.create("CON", "Constitution", constitution));
        stats.add(IntegerAttribute.create("INT", "Intelligence", intelligence));
        stats.add(IntegerAttribute.create("WIS", "Wisdom", wisdom));
        stats.add(IntegerAttribute.create("CHA", "Charisma", charisma));

        return stats;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("STR: ").append(getStrength()).append("\n");
        sb.append("DEX: ").append(getDexterity()).append("\n");
        sb.append("CON: ").append(getConstitution()).append("\n");
        sb.append("INT: ").append(getIntelligence()).append("\n");
        sb.append("WIS: ").append(getWisdom()).append("\n");
        sb.append("CHA: ").append(getCharisma());

        return sb.toString();
    }
}
