package com.zygon.rl.world.character;

import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.IntegerAttribute;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Player or NPC
 *
 * name tbd;
 *
 * @author zygon
 */
public class CharacterSheet {

    // This is maybe a little hokey?
    public static final String STATUS_PREFIX = "CHAR_STATUS_";

    // TODO: what do to about marshalling the entity to a character sheet?
    // this doesn't want to have all of the fields, it would rather
    // hold onto the Entity object but I worry about cache invalidation.
    private final String name;
//    private final UUID id;
    private final String description;
//    private final Location origin;
//    private final Location location;

    private final Stats stats;
    private final Status status;
    private final Set<Ability> abilities;
    private final Set<Spell> spells;

    public CharacterSheet(String name, String description, Stats stats, Status status,
            Set<Ability> abilities, Set<Spell> spells) {
        this.name = name;
        this.description = description;
        this.stats = stats;
        this.status = status;
        this.abilities = Collections.unmodifiableSet(abilities);
        this.spells = Collections.unmodifiableSet(spells);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
        return new CharacterSheet(name, description, stats, status, abilities, spells);
    }

    public CharacterSheet set(Set<Ability> abilities) {
        return new CharacterSheet(name, description, stats, status, abilities, spells);
    }

    public static CharacterSheet fromEntity(Entity entity) {
        // TODO: finish conversion
        return new CharacterSheet(
                entity.getName(),
                entity.getDescription(),
                toStats(entity),
                toStatus(entity),
                Set.of(),
                Set.of());
    }

    public Entity toEntity() {

        // abilities??? are we using reflection now??
        //
        Entity.Builder builder = Entities.PLAYER
                .copy()
                //                .setId(config.getPlayerUuid())
                .setName(getName())
                .setDescription(getDescription())
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
                entity.getAttributeNames().stream()
                        .filter(attrName -> attrName.startsWith(STATUS_PREFIX))
                        .map(attrName -> entity.getAttributeValue(attrName))
                        .collect(Collectors.toSet()));
    }

    // TODO: the rest of the setters as needed
}
