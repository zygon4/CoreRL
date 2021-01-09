package com.zygon.rl.world.character;

import com.zygon.rl.data.Element;
import com.zygon.rl.world.CommonAttributes;

import java.util.Collections;
import java.util.Set;

/**
 * Player or NPC
 *
 * name tbd;
 *
 * @author zygon
 */
public final class CharacterSheet extends Element {

    // This is maybe a little hokey?
    public static final String STATUS_PREFIX = "CHAR_STATUS_";

    private final Stats stats;
    private final Status status;
    private final Equipment equipment;
    private final Set<Ability> abilities;
    private final Set<Spell> spells;

    public CharacterSheet(String name, String description, Stats stats, Status status,
            Equipment equipment, Set<Ability> abilities, Set<Spell> spells) {
        setName(name);
        setDescription(description);
        this.stats = stats;
        this.status = status;
        this.equipment = equipment;
        this.abilities = Collections.unmodifiableSet(abilities);
        this.spells = Collections.unmodifiableSet(spells);
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

    public boolean isDead() {
        return getStatus().getEffects().containsKey(CommonAttributes.DEAD.name());
    }

    // TODO: maybe future - damage to a specific area
    public CharacterSheet loseHitPoints(int hps) {
        Status updateStatus = getStatus().decHitPoints(hps);
        if (updateStatus.getHitPoints() <= 0) {
            // A case of the deads.. in the future we could have other effects
            // here, or revive abilities, etc.
            updateStatus = updateStatus.addEffect(CommonAttributes.DEAD.name());
        }
        return set(updateStatus);
    }

    public CharacterSheet set(Status status) {
        CharacterSheet copy = new CharacterSheet(getName(), getDescription(), stats, status,
                equipment, abilities, spells);

        // TODO: this is a unreliable pattern
        copy.setId(getId());
        copy.setType(getType());

        return copy;
    }

    public CharacterSheet set(Set<Ability> abilities) {
        CharacterSheet copy = new CharacterSheet(getName(), getDescription(), stats, status,
                equipment, abilities, spells);

        // TODO: this is a unreliable pattern
        copy.setId(getId());
        copy.setType(getType());

        return copy;
    }
}
