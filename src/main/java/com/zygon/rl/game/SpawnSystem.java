package com.zygon.rl.game;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.data.npc.Npc;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldTile;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.SummonAction;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author zygon
 */
public class SpawnSystem extends GameSystem {

    private final Set<Location> spawnedLocations = new HashSet<>();
    private final Random random;

    public SpawnSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
        this.random = gameConfiguration.getRandom();
    }

    @Override
    public GameState apply(GameState state) {
        spawn(spawnedLocations, state, state.getWorld().getPlayerLocation());

        return state;
    }

    private void spawn(Set<Location> spawnedLocations, GameState state, Location center) {

        World world = state.getWorld();
        int freq = getGameConfiguration().getWorldSpawn().getFrequency();

        // round
        Location roundedCenter = Location.create(freq * (Math.round(center.getX() / freq)),
                freq * (Math.round(center.getY() / freq)));

        for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
            for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {

                Location location = Location.create(x, y);

                if (!spawnedLocations.contains(location)) {
                    int noiseX = -20 + random.nextInt(40);
                    int noiseY = -20 + random.nextInt(40);

                    Location rngLocation = Location.create(location.getX() + noiseX, y + noiseY);

                    Entity entity = world.getTerrain(rngLocation);
                    WorldTile wt = WorldTile.get(entity);

                    Action summonAction = null;

                    if (wt != WorldTile.PUDDLE) {

                        String creatureId = null;

                        if (random.nextDouble() > .10) {
                            creatureId = getRandomSetElement(Monster.getAllIds());
                        } else {
                            // TODO: set names on resulting spawns
                            creatureId = getRandomSetElement(Npc.getAllIds());
                        }

                        summonAction = new SummonAction(rngLocation, random.nextInt(4), creatureId, random);
                    }

                    if (summonAction != null && summonAction.canExecute(state)) {
                        summonAction.execute(state);
                    }

                    spawnedLocations.add(location);
                }
            }
        }
    }
    // spawn system needs a lot of params like what to spawn and where, how many of that
    // species, etc.

    // TODO: this is clearly insufficient, maybe stats based on size?
    private CharacterSheet getRandomPower(Element actor, int hitPoints) {
        int stats = random.nextInt(4) + 1;
        return new CharacterSheet(actor,
                new Stats(stats, stats, stats, stats, stats, stats),
                new Status(stats, hitPoints, Set.of()),
                null, Set.of(), Set.of());
    }

    private <E> E getRandomSetElement(Set<E> set) {
        return set.stream()
                .skip(random.nextInt(set.size()))
                .findFirst().orElse(null);
    }
}
