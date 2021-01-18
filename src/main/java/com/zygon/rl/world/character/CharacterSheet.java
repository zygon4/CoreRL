package com.zygon.rl.world.character;

import com.zygon.rl.data.Element;
import com.zygon.rl.world.Item;

import java.util.Collections;
import java.util.Map;
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

    // This should come from the race of character
    private static final Equipment STANDARD_EQ = Equipment.create(
            Map.of(Slot.HEAD, 1,
                    Slot.TORSO, 1,
                    Slot.ARM, 2,
                    Slot.HAND, 2,
                    Slot.RING, 4,
                    Slot.LEG, 2,
                    Slot.FOOT, 2));

    private final Element template;
    private final Stats stats;
    private final Status status;
    private final Equipment equipment;
    private final Inventory inventory;
    private final Set<Ability> abilities;
    private final Set<Spell> spells;

    public CharacterSheet(Element template, Stats stats, Status status,
            Equipment equipment, Inventory inventory, Set<Ability> abilities, Set<Spell> spells) {
        super(template);

        this.template = template;
        this.stats = stats;
        this.status = status;
        this.equipment = equipment != null ? equipment : STANDARD_EQ;
        this.inventory = inventory != null ? inventory : new Inventory();
        this.abilities = Collections.unmodifiableSet(abilities);
        this.spells = Collections.unmodifiableSet(spells);
    }

    public <T extends Element> T getElement() {
        return (T) template;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Inventory getInventory() {
        return inventory;
    }

    // TODO: Calculated from species/traits/status/eq
    public int getSpeed() {
        return 100;
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

    /**
     * Equips an item *from the inventory*.
     *
     * @param armor
     * @return
     */
    public CharacterSheet equip(Armor armor) {

        Item item = inventory.getItem(armor.getTemplate().getId());

        // get the item
        if (item != null) {
            Equipment newEq = equipment;

            if (equipment.canWear(armor)) {
                newEq = equipment.wear(armor);
            }

            return new CharacterSheet(template, stats, status,
                    newEq, inventory.remove(item), abilities, spells);

        }

        return null;
    }

    public CharacterSheet add(Item item) {
        CharacterSheet copy = new CharacterSheet(template, stats, status,
                equipment, inventory.add(item), abilities, spells);

        return copy;
    }

    public CharacterSheet remove(Item item) {
        // TODO: drop equipped/wielded
        CharacterSheet copy = new CharacterSheet(template, stats, status,
                equipment, inventory.remove(item), abilities, spells);

        return copy;
    }

    // hopefully not necessary, use the helper functions
    public CharacterSheet set(Equipment equipment) {
        CharacterSheet copy = new CharacterSheet(template, stats, status,
                equipment, inventory, abilities, spells);

        return copy;
    }

    public CharacterSheet set(Status status) {
        CharacterSheet copy = new CharacterSheet(template, stats, status,
                equipment, inventory, abilities, spells);

        return copy;
    }

    public CharacterSheet set(Set<Ability> abilities) {
        CharacterSheet copy = new CharacterSheet(template, stats, status,
                equipment, inventory, abilities, spells);

        return copy;
    }

    public CharacterSheet wield(Weapon weapon) {

        Item item = inventory.getItem(weapon.getTemplate().getId());

        // get the item
        if (item != null) {
            Equipment newEq = equipment.wield(weapon);

            if (newEq == null) {
                return null;
            }

            return new CharacterSheet(template, stats, status,
                    newEq, inventory.remove(item), abilities, spells);

        }

        return null;
    }
}
