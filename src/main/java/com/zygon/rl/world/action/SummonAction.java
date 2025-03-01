package com.zygon.rl.world.action;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.stewsters.util.name.FantasyNameGen;
import com.zygon.rl.data.Creature;
import com.zygon.rl.data.Effect;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;
import com.zygon.rl.world.character.StatusEffect;

/**
 *
 * @author zygon
 */
public class SummonAction extends Action {

    // Maybe wants to be a parameter?
    private static final int MAX_RADIUS = 5;

    private final Location location;
    private final int count;
    private final String id;
    private final Set<Effect> effects;
    private final Random random;

    // This is intended to be a actor-only summon, but summoning random items
    // is pretty valid as well. This will need enhancement.
    public SummonAction(Location location, int count, String id,
            Set<Effect> effects, Random random) {
        this.location = location;
        this.count = count;
        this.id = id;
        this.effects = effects != null
                ? Collections.unmodifiableSet(effects) : Collections.emptySet();
        this.random = random;
    }

    public SummonAction(Location location, int count, String id, Random random) {
        this(location, count, id, null, random);
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

        List<Location> legalLocations = location.getNeighbors(MAX_RADIUS).stream()
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

        return new CharacterSheet(actor,
                FantasyNameGen.generate(),
                new Stats(stats, stats, stats, stats, stats, stats),
                new Status(stats, actor.getHitPoints(), statusEffects),
                null, null, Set.of(), Set.of());
    }
}
