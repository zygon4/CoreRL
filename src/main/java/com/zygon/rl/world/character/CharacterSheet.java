package com.zygon.rl.world.character;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zygon.rl.data.Creature;
import com.zygon.rl.data.Effect;
import com.zygon.rl.util.dialog.Dialog;
import com.zygon.rl.util.quest.QuestInfo;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Tangible;
import com.zygon.rl.world.action.Action;

/**
 * Represents any kind of "actor" in the game.
 *
 *
 * name tbd;
 *
 * @author zygon
 */
public final class CharacterSheet extends Tangible {

    // This should come from the race of character
    private static final Equipment STANDARD_EQ = Equipment.create(
            Map.of(Slot.HEAD, 1,
                    Slot.TORSO, 1,
                    Slot.ARM, 2,
                    Slot.HAND, 2,
                    Slot.RING, 4,
                    Slot.LEG, 2,
                    Slot.FOOT, 2));

    private final String name;
    private final Stats stats;
    private final Status status;
    private final Equipment equipment;
    private final Inventory inventory;
    private final Set<Ability> abilities;
    private final Set<Spell> spells;
    private final Set<Proficiency> proficiencies;
    private final Dialog dialog;
    private final Map<TriggerType, Action> triggers;
    private final Set<QuestInfo> quests;

    // What else?
    public enum TriggerType {
        DEATH
    }

    public CharacterSheet(Creature template, String name, Stats stats,
            Status status, Equipment equipment, Inventory inventory,
            Set<Ability> abilities, Set<Spell> spells,
            Set<Proficiency> proficiencies,
            Dialog dialog, Map<TriggerType, Action> triggers,
            Set<QuestInfo> quests) {
        super(template);

        this.name = name;
        this.stats = stats;
        this.status = status;
        this.equipment = equipment != null ? equipment : STANDARD_EQ;
        this.inventory = inventory != null ? inventory : new Inventory();
        this.abilities = Collections.unmodifiableSet(abilities);
        this.spells = Collections.unmodifiableSet(spells);
        this.proficiencies = Collections.unmodifiableSet(proficiencies);
        this.dialog = dialog;
        this.triggers = Collections.unmodifiableMap(triggers);
        this.quests = Collections.unmodifiableSet(quests);
    }

    public CharacterSheet(Creature template, String name, Stats stats,
            Status status, Equipment equipment, Inventory inventory,
            Set<Ability> abilities, Set<Spell> spells,
            Set<Proficiency> proficiencies) {
        this(template, name, stats, status, equipment, inventory, abilities,
                spells, proficiencies, null, Map.of(), Set.of());
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getSpecies() {
        Creature creature = getTemplate();
        return creature.getSpecies();
    }

    // TODO: Calculate final from species/traits/status/eq
    public int getSpeed() {
        Creature creature = getTemplate();
        int baseSpeed = creature.getSpeed();

        // basically sprinting/running..
        if (getStatus().isEffected(Effect.EffectNames.ENHANCED_SPEED.getId())) {
            // This is
            StatusEffect speedBuff = getStatus().getEffects()
                    .get(Effect.EffectNames.ENHANCED_SPEED.getId());
            Integer speedAdjustment = speedBuff.getEffect().getStatMods().stream()
                    .filter(sm -> sm.getName().equals("SPD"))
                    .map(sm -> sm.getAmount())
                    .findFirst().orElse(0);
            return baseSpeed + speedAdjustment;
        }

        return baseSpeed;
    }

    public Stats getStats() {

        Stats baseStats = stats;

        int strMod = 0;
        int dexMod = 0;
        int conMod = 0;
        int intMod = 0;
        int wisMod = 0;
        int chaMod = 0;

        for (StatusEffect se : getStatus().getEffects().values()) {
            List<Effect.StatMod> statMods = se.getEffect().getStatMods();
            if (statMods != null) {
                for (Effect.StatMod mod : statMods) {
                    switch (mod.getName()) {
                        case "STR" ->
                            strMod += mod.getAmount();
                        case "DEX" ->
                            dexMod += mod.getAmount();
                        case "CON" ->
                            conMod += mod.getAmount();
                        case "INT" ->
                            intMod += mod.getAmount();
                        case "WIS" ->
                            wisMod += mod.getAmount();
                        case "CHA" ->
                            chaMod += mod.getAmount();
                    }
                }

                baseStats = new Stats(
                        baseStats.getStrength() + strMod,
                        baseStats.getDexterity() + dexMod,
                        baseStats.getConstitution() + conMod,
                        baseStats.getIntelligence() + intMod,
                        baseStats.getWisdom() + wisMod,
                        baseStats.getCharisma() + chaMod);
            }
        }

        return baseStats;
    }

    public Status getStatus() {
        return status;
    }

    public Set<Ability> getAbilities() {
        return abilities;
    }

    @Override
    public String getName() {
        return name;
    }

    public Set<Spell> getSpells() {
        return spells;
    }

    public Set<Proficiency> getProficiencies() {
        return proficiencies;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public Map<TriggerType, Action> getTriggers() {
        return triggers;
    }

    public Set<QuestInfo> getQuests() {
        return quests;
    }

    public boolean isDead() {
        return getStatus().getHitPoints() <= 0;
    }

    public CharacterSheet gainHitPoints(int hps) {
        Creature creature = getTemplate();

        int maxHp = creature.getHitPoints();
        int currentHp = getStatus().getHitPoints();

        if (currentHp < maxHp) {
            int adjustment = hps;
            if (currentHp + adjustment > maxHp) {
                // This all feels clunky but it should work.. just subtract the overage from the adjustment.
                adjustment -= (currentHp + adjustment) - maxHp;
            }
            return set(getStatus().incHitPoints(adjustment));
        }

        return this;
    }

    // TODO: maybe future - damage to a specific area
    public CharacterSheet loseHitPoints(int hps) {
        return set(getStatus().decHitPoints(hps));
    }

    // Call before acting
    public CharacterSheet powerUp() {
        int boost = getSpeed();
        return set(getStatus().incEnergy(boost));
    }

    // Call after acting
    public CharacterSheet coolDown() {
        return set(getStatus().resetEnergy());
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

            return new CharacterSheet(getTemplate(), name, stats, status, newEq,
                    inventory.remove(item), abilities, spells, proficiencies, dialog, triggers, quests);

        }

        return null;
    }

    public CharacterSheet add(Item item) {
        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory.add(item), abilities, spells, proficiencies, dialog, triggers, quests);

        return copy;
    }

    public CharacterSheet remove(Item item) {
        // TODO: drop equipped/wielded
        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory.remove(item), abilities, spells, proficiencies, dialog, triggers, quests);

        return copy;
    }

    // hopefully not necessary, use the helper functions
    public CharacterSheet set(Equipment equipment) {
        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory, abilities, spells, proficiencies, dialog, triggers, quests);

        return copy;
    }

    public CharacterSheet set(Status status) {
        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory, abilities, spells, proficiencies, dialog, triggers, quests);

        return copy;
    }

    public CharacterSheet set(Set<Ability> abilities) {
        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory, abilities, spells, proficiencies, dialog, triggers, quests);

        return copy;
    }

    public CharacterSheet set(Proficiency proficiency) {

        Set<Proficiency> newProfs = new LinkedHashSet<>();
        for (var prof : this.proficiencies) {
            if (prof.getProficiency().getId()
                    .equals(proficiency.getProficiency().getId())) {
                newProfs.add(proficiency);
            } else {
                newProfs.add(prof);
            }
        }

        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory, abilities, spells, newProfs, dialog, triggers, quests);

        return copy;
    }

    public CharacterSheet set(Dialog dialog) {
        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory, abilities, spells, proficiencies, dialog, triggers, quests);

        return copy;
    }

    public CharacterSheet set(Creature creature) {
        CharacterSheet copy = new CharacterSheet(creature, name, stats, status,
                equipment, inventory, abilities, spells, proficiencies, dialog, triggers, quests);

        return copy;
    }

    /**
     * Replaces the built-in triggers.
     *
     * @param triggers
     * @return
     */
    public CharacterSheet set(Map<TriggerType, Action> triggers) {
        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory, abilities, spells, proficiencies, dialog, triggers, quests);

        return copy;
    }

    /**
     * Add a quest.
     *
     * @param quest
     * @return
     */
    public CharacterSheet add(QuestInfo quest) {
        Set<QuestInfo> allQuests = new LinkedHashSet<>();
        allQuests.addAll(this.getQuests());
        allQuests.add(quest);
        CharacterSheet copy = new CharacterSheet(getTemplate(), name, stats, status,
                equipment, inventory, abilities, spells, proficiencies, dialog, triggers, allQuests);

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

            return new CharacterSheet(getTemplate(), name, stats, status, newEq,
                    inventory.remove(item), abilities, spells, proficiencies, dialog, triggers, quests);

        }

        return null;
    }
}
