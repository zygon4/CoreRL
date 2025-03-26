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
    private final Progress progress;
    private final Set<Proficiency> proficiencies;
    private final Dialog dialog;
    private final Map<TriggerType, Action> triggers;
    private final Set<QuestInfo> quests;

    // What else?
    public enum TriggerType {
        DEATH
    }

    private CharacterSheet(Builder builder) {
        super(builder.template);
        this.name = builder.name;
        this.stats = builder.stats;
        this.status = builder.status;
        this.equipment = builder.equipment != null ? builder.equipment : STANDARD_EQ;
        this.inventory = builder.inventory != null ? builder.inventory : new Inventory();
        this.abilities = builder.abilities != null ? Collections.unmodifiableSet(builder.abilities) : Collections.emptySet();
        this.progress = builder.progress != null ? builder.progress : Progress.create();
        this.proficiencies = builder.proficiencies != null ? Collections.unmodifiableSet(builder.proficiencies) : Collections.emptySet();
        this.dialog = builder.dialog;
        this.triggers = builder.triggers != null ? Collections.unmodifiableMap(builder.triggers) : Collections.emptyMap();
        this.quests = builder.quests != null ? Collections.unmodifiableSet(builder.quests) : Collections.emptySet();
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

    public Stats getModifiedStats() {
        Stats baseStats = getStats();

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

    public Stats getStats() {
        return stats;
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

    public Progress getProgress() {
        return progress;
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

            return copy()
                    .equipment(newEq)
                    .inventory(inventory.remove(item))
                    .build();
        }

        return null;
    }

    public CharacterSheet add(Item item) {
        return copy()
                .inventory(inventory.add(item))
                .build();
    }

    public CharacterSheet remove(Item item) {
        return copy()
                .inventory(inventory.remove(item))
                .build();
        // TODO: drop equipped/wielded
    }

    // hopefully not necessary, use the helper functions
    public CharacterSheet set(Equipment equipment) {
        return copy()
                .equipment(equipment)
                .build();
    }

    public CharacterSheet set(Status status) {
        return copy()
                .status(status)
                .build();
    }

    public CharacterSheet set(Set<Ability> abilities) {
        return copy()
                .abilties(abilities)
                .build();
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

        return copy()
                .proficiencies(newProfs)
                .build();
    }

    public CharacterSheet set(Dialog dialog) {
        return copy()
                .dialog(dialog)
                .build();
    }

    public CharacterSheet set(Creature creature) {
        return copy()
                .template(creature)
                .build();
    }

    /**
     * Replaces the built-in triggers.
     *
     * @param triggers
     * @return
     */
    public CharacterSheet set(Map<TriggerType, Action> triggers) {
        return copy()
                .triggers(triggers)
                .build();
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

        return copy()
                .quests(allQuests)
                .build();
    }

    public CharacterSheet wield(Weapon weapon) {

        Item item = inventory.getItem(weapon.getTemplate().getId());

        // get the item
        if (item != null) {
            Equipment newEq = equipment.wield(weapon);

            if (newEq == null) {
                return null;
            }

            return copy()
                    .equipment(newEq)
                    .inventory(inventory.remove(item))
                    .build();
        }

        return null;
    }

    public static Builder create(final Creature template, final String name,
            final Stats stats, final Status status) {
        return new Builder()
                .template(template)
                .name(name)
                .stats(stats)
                .status(status);
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static class Builder {

        private Creature template;
        private String name;
        private Stats stats;
        private Status status;
        private Equipment equipment;
        private Inventory inventory;
        private Set<Ability> abilities;
        private Progress progress;
        private Set<Proficiency> proficiencies;
        private Dialog dialog;
        private Map<TriggerType, Action> triggers;
        private Set<QuestInfo> quests;

        private Builder(CharacterSheet sheet) {
            this.template = sheet.getTemplate();
            this.name = sheet.getName();
            this.stats = sheet.getStats();
            this.status = sheet.getStatus();
            this.equipment = sheet.getEquipment();
            this.inventory = sheet.getInventory();
            this.abilities = sheet.getAbilities();
            this.progress = sheet.getProgress();
            this.proficiencies = sheet.getProficiencies();
            this.dialog = sheet.getDialog();
            this.triggers = sheet.getTriggers();
            this.quests = sheet.getQuests();
        }

        private Builder() {

        }

        public Builder template(Creature template) {
            this.template = template;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder stats(Stats stats) {
            this.stats = stats;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder equipment(Equipment equipment) {
            this.equipment = equipment;
            return this;
        }

        public Builder inventory(Inventory inventory) {
            this.inventory = inventory;
            return this;
        }

        public Builder abilties(Set<Ability> abilities) {
            this.abilities = abilities;
            return this;
        }

        public Builder progress(Progress progress) {
            this.progress = progress;
            return this;
        }

        public Builder proficiencies(Set<Proficiency> proficiencies) {
            this.proficiencies = proficiencies;
            return this;
        }

        public Builder dialog(Dialog dialog) {
            this.dialog = dialog;
            return this;
        }

        public Builder triggers(Map<TriggerType, Action> triggers) {
            this.triggers = triggers;
            return this;
        }

        public Builder quests(Set<QuestInfo> quests) {
            this.quests = quests;
            return this;
        }

        public CharacterSheet build() {
            return new CharacterSheet(this);
        }
    }
}
