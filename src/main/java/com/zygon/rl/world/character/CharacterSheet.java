package com.zygon.rl.world.character;

import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.BooleanAttribute;
import com.zygon.rl.world.CommonAttributes;
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
    private final int age;

    private final Stats stats;
    private final Status status;
    private final Set<Ability> abilities;
    private final Set<Spell> spells;

    public CharacterSheet(String name, int age, Stats stats, Status status,
            Set<Ability> abilities, Set<Spell> spells) {
        this.name = name;
        this.age = age;
        this.stats = stats;
        this.status = status;
        this.abilities = Collections.unmodifiableSet(abilities);
        this.spells = Collections.unmodifiableSet(spells);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
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

    public CharacterSheet setAge(int age) {
        return new CharacterSheet(name, age, stats, status, abilities, spells);
    }

    public CharacterSheet set(int age) {
        return new CharacterSheet(name, age, stats, status, abilities, spells);
    }

    public CharacterSheet set(Set<Ability> abilities) {
        return new CharacterSheet(name, age, stats, status, abilities, spells);
    }

    public static CharacterSheet fromEntity(Entity entity) {
        // TODO: finish conversion
        return new CharacterSheet(
                entity.getName(),
                14, new Stats(0, 0, 0, 0, 0),
                new Status(0, Set.of()),
                Set.of(),
                Set.of());

    }

    // doing a deep conversion will be a pain
    // need to have some tools/functions to make this easier.
    // e.g. convert the entire status object into a set of attributes
    public Entity toEntity() {

        // abilities??? are we using reflection now??
        //
        return Entities.PLAYER
                .copy()
                //                .setId(config.getPlayerUuid())
                .setName(getName())
                .setDescription("fierce something")
                //                .setLocation(Location.create(0, 0))
                .addAttributes(IntegerAttribute.create(
                        Attribute.builder()
                                .setName(CommonAttributes.HEALTH.name())
                                .setDescription("Health")
                                .setValue(String.valueOf(getStatus().getHitPoints()))
                                .build()))
                .addAttributes(BooleanAttribute.create(Attribute.builder()
                        .setName(CommonAttributes.LIVING.name()).build(), true))
                .build();
    }

    // TODO: the rest of the setters as needed
}
