package com.zygon.rl.world.character;

import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.IntegerAttribute;

import java.util.LinkedHashSet;
import java.util.Set;

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
