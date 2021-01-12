package com.zygon.rl.world.character;

import com.zygon.rl.data.Element;

import java.util.Collections;
import java.util.Set;

/**
 * Represents any kind of "actor" in the game.
 *
 *
 * name tbd;
 *
 * @author zygon
 */
public final class CharacterSheet extends Element {

    private final Element template;
    private final Stats stats;
    private final Status status;
    private final Equipment equipment;
    private final Set<Ability> abilities;
    private final Set<Spell> spells;

    public CharacterSheet(Element template, Stats stats, Status status,
            Equipment equipment, Set<Ability> abilities, Set<Spell> spells) {
        super(template);

        this.template = template;
        this.stats = stats;
        this.status = status;
        this.equipment = equipment != null ? equipment : new Equipment(null);
        this.abilities = Collections.unmodifiableSet(abilities);
        this.spells = Collections.unmodifiableSet(spells);
    }

    public <T extends Element> T getElement() {
        return (T) template;
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

    public boolean isDead() {
        return getStatus().getHitPoints() <= 0;
    }

    // TODO: maybe future - damage to a specific area
    public CharacterSheet loseHitPoints(int hps) {
        return set(getStatus().decHitPoints(hps));
    }

    public CharacterSheet set(Status status) {
        CharacterSheet copy = new CharacterSheet(template, stats, status,
                equipment, abilities, spells);

        return copy;
    }

    public CharacterSheet set(Set<Ability> abilities) {
        CharacterSheet copy = new CharacterSheet(template, stats, status,
                equipment, abilities, spells);

        return copy;
    }
}
