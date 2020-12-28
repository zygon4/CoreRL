package com.zygon.rl.world.character;

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

    public Stats(int strength, int dexterity, int constitution, int intelligence, int wisdom) {
        this.strength = strength;
        this.dexterity = dexterity;
        this.constitution = constitution;
        this.intelligence = intelligence;
        this.wisdom = wisdom;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("STR: ").append(getStrength()).append("\n");
        sb.append("DEX: ").append(getDexterity()).append("\n");
        sb.append("CON: ").append(getConstitution()).append("\n");
        sb.append("INT: ").append(getIntelligence()).append("\n");
        sb.append("WIS: ").append(getWisdom());

        return sb.toString();
    }
}
