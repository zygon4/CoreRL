package com.zygon.rl.game;

import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.data.npc.Npc;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.SummonAction;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author zygon
 */
public class SpawnSystem extends GameSystem {

    // This can theoretically grow unbounded, or maybe it should very slowly
    // age out
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
                    Terrain terrain = world.getTerrain(location);
                    Action summonAction = null;

                    if (!terrain.getId().equals(Terrain.Ids.PUDDLE.getId())) {

                        String creatureId = null;

                        if (random.nextDouble() > .10) {
                            creatureId = getRandomSetElement(Monster.getAllIds());
                        } else {
                            // TODO: set names on resulting spawns
                            // TODO: equipment/items on NPCs
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

    private <E> E getRandomSetElement(Set<E> set) {
        return set.stream()
                .skip(random.nextInt(set.size()))
                .findFirst().orElse(null);
    }
}
