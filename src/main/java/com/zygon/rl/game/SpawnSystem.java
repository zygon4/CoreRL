package com.zygon.rl.game;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.data.npc.Npc;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldTile;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        spawn(spawnedLocations, state.getWorld(), state.getWorld().getPlayerLocation());

        return state;
    }

    private void spawn(Set<Location> spawnedLocations, World world, Location center) {

        // round
        Location roundedCenter = Location.create(25 * (Math.round(center.getX() / 25)),
                25 * (Math.round(center.getY() / 25)));

        for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= 25, realY++) {
            for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += 25, realX++) {

                Location location = Location.create(x, y);

                if (!spawnedLocations.contains(location)) {
                    int noiseX = -20 + random.nextInt(40);
                    int noiseY = -20 + random.nextInt(40);

                    Location rngLocation = Location.create(location.getX() + noiseX, y + noiseY);

                    Entity entity = world.getTerrain(rngLocation);
                    WorldTile wt = WorldTile.get(entity);

                    if (wt != WorldTile.PUDDLE) {
                        List<CharacterSheet> characters = new ArrayList<>();
                        if (random.nextDouble() > .10) {
                            String monsterId = getRandomSetElement(Monster.getAllIds());
                            Monster monster = Monster.get(monsterId);

                            for (int i = 0; i < random.nextInt(3); i++) {
                                characters.add(getRandomPower(monster, monster.getHitPoints()));
                            }
                        } else {
                            String npcId = getRandomSetElement(Npc.getAllIds());

                            for (int i = 0; i < random.nextInt(3); i++) {
                                characters.add(getRandomPower(Npc.get(npcId), random.nextInt(20) + 10));
                            }
                        }
                        characters.forEach(cs -> world.add(cs, location));
                    }

                    spawnedLocations.add(location);
                }
            }
        }
    }
    // spawn system needs a lot of params like what to spawn and where, how many of that
    // species, etc.

    // TODO: this is clearly insufficient, maybe stats based on size?
    private CharacterSheet getRandomPower(Element monster, int hitPoints) {
        int stats = random.nextInt(4) + 1;
        return new CharacterSheet(monster,
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
