package com.zygon.rl.world.action;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.stewsters.util.name.FantasyNameGen;
import com.zygon.rl.data.Creature;
import com.zygon.rl.data.Effect;
import com.zygon.rl.data.PoolData;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.ArmorData;
import com.zygon.rl.data.items.Melee;
import com.zygon.rl.game.GameState;
import com.zygon.rl.util.dialog.Dialog;
import com.zygon.rl.util.dialog.DialogChoice;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.Armor;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Pool;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.StatusEffect;
import com.zygon.rl.world.character.Weapon;

/**
 * Summons a 1+ group of same-type monsters/NPCS around the location specified.
 *
 * @author zygon
 */
public class SummonAction extends Action {

    public static final int DEFAULT_RADIUS = 5;

    private final Location location;
    private final int count;
    private final int radius;
    private final String id;
    private final Set<Effect> effects;
    private final Random random;

    // This is intended to be a actor-only summon, but summoning random items
    // is pretty valid as well. This will need enhancement.
    public SummonAction(Location location, int count, int radius, String id,
            Set<Effect> effects, Random random) {
        this.location = location;
        this.count = count;
        this.radius = radius;
        this.id = id;
        this.effects = effects != null
                ? Collections.unmodifiableSet(effects) : Collections.emptySet();
        this.random = random;
    }

    public SummonAction(Location location, int count, int radius, String id,
            Random random) {
        this(location, count, radius, id, null, random);
    }

    public SummonAction(Location location, int count, String id, Random random) {
        this(location, count, DEFAULT_RADIUS, id, null, random);
    }

    @Override
    public boolean canExecute(GameState state) {
        return getSummonLocations(count, state.getWorld()).size() == count;
    }

    @Override
    public GameState execute(GameState state) {
        Set<Location> summonLocations = getSummonLocations(count, state.getWorld());

        Creature actor = Data.get(id);

        for (Location loc : summonLocations) {
            CharacterSheet generated = getRandomCharacter(state, actor);
            state.getWorld().add(generated, loc);
        }

        return state;
    }

    private Set<Location> getSummonLocations(int count, World world) {
        Set<Location> locations = new HashSet<>();

        List<Location> legalLocations = location.getNeighbors(radius, true).stream()
                .filter(world::canMove)
                .collect(Collectors.toList());

        if (legalLocations.size() >= count) {
            Collections.shuffle(legalLocations, random);

            for (int i = 0; i < count; i++) {
                locations.add(legalLocations.get(i));
            }
        }

        return locations;
    }

    private CharacterSheet getRandomCharacter(GameState state, Creature creature) {
        final int age = 1 + random.nextInt(4);

        Set<Pool> pools = creature.getPools().stream()
                .map(id -> PoolData.get(id))
                .map(e -> Pool.createMax(e))
                .collect(Collectors.toSet());
        Set<StatusEffect> statusEffects = effects.stream()
                .map(e -> new StatusEffect(e, state.getTurnCount()))
                .collect(Collectors.toSet());

        final String name;
        final Dialog dialog;
        Weapon weapon = null;

        if (creature.getSpecies().equals(CommonAttributes.HUMAN.name())) {
            name = FantasyNameGen.generate();
            weapon = generate(creature);

            Dialog start = Dialog.create("Greetings traveller. You look mighty pale today..");
            // I want them to hostile but don't have enough context here..
            Dialog demon = Dialog.create("Monster!", Optional.empty());
            Dialog disguised = Dialog.create("I see.. better health to you.. stay indoors.", Optional.empty());

            DialogChoice yes = DialogChoice.create("But I feel great..", Optional.empty(), Optional.of(demon));
            DialogChoice no = DialogChoice.create("I am but sick.. goodbye..", Optional.empty(), Optional.of(disguised));

            dialog = start.addChoices(List.of(yes, no));
        } else {
            name = creature.getName();
            dialog = Dialog.create("...");
        }

        CharacterSheet character = createBase(
                creature,
                generate(creature, random),
                new Status(age, pools, statusEffects),
                weapon,
                name);

        // TODO: proc gen dialog);
        return character.set(dialog);
    }

    // This leaves a lot to be desired, need to drive armor/eq from JSON data..
    CharacterSheet createBase(Creature species, Stats stats, Status status,
            Weapon weapon, String name) {

        CharacterSheet pc = CharacterSheet
                .create(species, name, stats, status)
                .build();

        if (species.getSpecies().equals("HUMAN")) {
            ArmorData dataTunic = ArmorData.get("torso_tunic_plain");
            ArmorData dataBoots = ArmorData.get("boots_leather");
            ArmorData dataPants = ArmorData.get("legs_cotton_pants");
            ArmorData dataArms = ArmorData.get("arms_leather_bracers");

            Armor arms = new Armor(dataArms);
            Armor tunic = new Armor(dataTunic);
            Armor pants = new Armor(dataPants);
            Armor boots = new Armor(dataBoots);

            pc = pc.add(boots).equip(boots);
            pc = pc.add(pants).equip(pants);
            pc = pc.add(tunic).equip(tunic);
            pc = pc.add(arms).equip(arms);

            if (weapon != null) {
                pc = pc.add(weapon).wield(weapon);
            }
        }

        return pc;
    }

    static Stats generate(Creature creature, Random random) {

        final int baseStat = 8;
        Supplier<Integer> getStatFn = () -> baseStat + random.nextInt(4);

        final int weight = creature.getWeight();
        final int weightMod = weight / 100;

        return new Stats()
                .incCha(getStatFn.get())
                .incCon(getStatFn.get() + weightMod)
                .incDex(getStatFn.get())
                .incInt(getStatFn.get())
                .incStr(getStatFn.get() + weightMod)
                .incWis(getStatFn.get());
    }

    static Weapon generate(Creature creature) {

        switch (creature.getId()) {
            case "npc_generic_soldier":
                return new Weapon(Melee.get("sword"), 18, 4, 0);
            case "npc_generic_farmer":
                return new Weapon(Melee.get("scythe"), 18, 4, 0);
            default:
                return new Weapon(Melee.get("dagger"), 20, 2, 0);
        }
    }
}
