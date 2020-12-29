package com.zygon.rl.world.character;

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
public class CharacterSheet {

    private final String name;

    private final Stats stats;
    private final Status status;
    private final Set<Ability> abilities;
    private final Set<Spell> spells;

    public CharacterSheet(String name, Stats stats, Status status,
            Set<Ability> abilities, Set<Spell> spells) {
        this.name = name;
        this.stats = stats;
        this.status = status;
        this.abilities = Collections.unmodifiableSet(abilities);
        this.spells = Collections.unmodifiableSet(spells);
    }

    public String getName() {
        return name;
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

    public CharacterSheet set(Set<Ability> abilities) {
        return new CharacterSheet(name, stats, status, abilities, spells);
    }

    public static CharacterSheet fromEntity(Entity entity) {
        // TODO: finish conversion
        return new CharacterSheet(
                entity.getName(),
                toStats(entity),
                toStatus(entity),
                Set.of(),
                Set.of());
    }

    // doing a deep conversion will be a pain
    // need to have some tools/functions to make this easier.
    // e.g. convert the entire status object into a set of attributes
    public Entity toEntity() {

        // abilities??? are we using reflection now??
        //
        Entity.Builder builder = Entities.PLAYER
                .copy()
                //                .setId(config.getPlayerUuid())
                .setName(getName())
                .setDescription("fierce something");

        getStatus().getAttributes().forEach(status -> {
            builder.addAttributes(status);
        });

        getStats().getAttributes().forEach(stat -> {
            builder.addAttributes(stat);
        });

        return builder.build();
    }

    private static Stats toStats(Entity entity) {
        return new Stats(
                IntegerAttribute.getValue(entity.getAttribute("STR")),
                IntegerAttribute.getValue(entity.getAttribute("DEX")),
                IntegerAttribute.getValue(entity.getAttribute("CON")),
                IntegerAttribute.getValue(entity.getAttribute("INT")),
                IntegerAttribute.getValue(entity.getAttribute("WIS")));
    }

    private static Status toStatus(Entity entity) {
        return new Status(
                IntegerAttribute.getValue(entity.getAttribute("Age")),
                IntegerAttribute.getValue(entity.getAttribute("HP")),
                Set.of());
    }

    // TODO: the rest of the setters as needed
}
