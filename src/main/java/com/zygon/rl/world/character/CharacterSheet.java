package com.zygon.rl.world.character;

import com.zygon.rl.data.Element;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.IntegerAttribute;

import java.util.Collections;
import java.util.Set;

/**
 * Player or NPC
 *
 * name tbd;
 *
 * @author zygon
 */
public class CharacterSheet extends Element {

    // This is maybe a little hokey?
    public static final String STATUS_PREFIX = "CHAR_STATUS_";

    private final Stats stats;
    private final Status status;
    private final Equipment equipment;
    private final Set<Ability> abilities;
    private final Set<Spell> spells;

    public CharacterSheet(String name, String description, Stats stats, Status status,
            Equipment equipment, Set<Ability> abilities, Set<Spell> spells) {
        setId("player");
        setName(name);
        setDescription(description);
        this.stats = stats;
        this.status = status;
        this.equipment = equipment;
        this.abilities = Collections.unmodifiableSet(abilities);
        this.spells = Collections.unmodifiableSet(spells);
    }

    @Override
    public String getType() {
        return "player";
    }

    @Override
    public String getSymbol() {
        return "@";
    }

    @Override
    public String getColor() {
        return "red";
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Stats getStats() {
        return stats;
    }

    public Status getStatus() {
        return status;
    }

    public Set<Ability> getAbilities() {
        return abilities;
    }

    public Set<Spell> getSpells() {
        return spells;
    }

    public CharacterSheet set(Status status) {
        return new CharacterSheet(getName(), getDescription(), stats, status, equipment, abilities, spells);
    }

    public CharacterSheet set(Set<Ability> abilities) {
        return new CharacterSheet(getName(), getDescription(), stats, status, equipment, abilities, spells);
    }

    @Deprecated
    public Entity toEntity() {

        // abilities??? are we using reflection now??
        //
        Entity.Builder builder = Entities.PLAYER
                .copy()
                //                .setId(config.getPlayerUuid())
                .setName(getName())
                .setDescription(getDescription())
                .setDescription("fierce something");

        return builder.build();
    }

    @Deprecated
    private static Stats toStats(Entity entity) {
        return new Stats(
                IntegerAttribute.getValue(entity.getAttribute("STR")),
                IntegerAttribute.getValue(entity.getAttribute("DEX")),
                IntegerAttribute.getValue(entity.getAttribute("CON")),
                IntegerAttribute.getValue(entity.getAttribute("INT")),
                IntegerAttribute.getValue(entity.getAttribute("WIS")),
                IntegerAttribute.getValue(entity.getAttribute("CHA")));
    }

    private static Equipment toEquipment(Entity entity) {
        return new Equipment(new Weapon(20, 2, 4, 1, 0, 0, 0));
    }

    // TODO: the rest of the setters as needed
}
