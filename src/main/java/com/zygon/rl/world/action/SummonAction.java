package com.zygon.rl.world.action;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.stewsters.util.name.FantasyNameGen;
import com.zygon.rl.data.Creature;
import com.zygon.rl.data.Effect;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.game.GameState;
import com.zygon.rl.util.dialog.Dialog;
import com.zygon.rl.util.dialog.DialogChoice;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.StatusEffect;

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

    // TODO: this is clearly insufficient, maybe stats based on size?
    private CharacterSheet getRandomCharacter(GameState state, Creature actor) {
        int stats = random.nextInt(4) + 1;
        Set<StatusEffect> statusEffects = effects.stream()
                .map(e -> new StatusEffect(e, state.getTurnCount()))
                .collect(Collectors.toSet());

        Dialog start = Dialog.create("Greetings traveller. Pick a world..");
        Dialog darkness = Dialog.create("Ah Darkness is excellent..", Optional.empty());
//                Dialog darkness2 = Dialog.create("Yess.. the darkness binds us.. ", Optional.empty());
        Dialog light = Dialog.create("I see. Light it is..", Optional.empty());

        DialogChoice leafDark = DialogChoice.create("I pick the world of darkness", Optional.empty(), Optional.of(darkness));
        DialogChoice leafDark2 = DialogChoice.create("I serve the darkness.. goodbye..", Optional.empty(), Optional.empty());
        DialogChoice leafLight = DialogChoice.create("I pick the world of light.", Optional.empty(), Optional.of(light));
        DialogChoice leafLoop = DialogChoice.create("I pick the world of unending", Optional.empty(), Optional.of(start));

        darkness = darkness.addChoices(List.of(leafDark2));
        start = start.addChoices(List.of(leafDark.set(Optional.of(darkness)), leafLight, leafLoop));

        String name = FantasyNameGen.generate();
        CharacterSheet rando = new CharacterSheet(actor,
                name,
                new Stats(stats, stats, stats, stats, stats, stats),
                new Status(stats, actor.getHitPoints(), statusEffects),
                null, null, Set.of(), Set.of());
        //TODO: proc gen dialog);
        return rando.set(start);
    }
}
