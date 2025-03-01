package com.zygon.rl.game.systems;

import com.zygon.rl.data.ItemClass;
import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.buildings.Building;
import com.zygon.rl.data.buildings.BuildingData;
import com.zygon.rl.data.buildings.Layout;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.ArmorData;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.data.npc.Npc;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.GameSystem;
import com.zygon.rl.game.SpawnContext;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldRegion;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.SetItemAction;
import com.zygon.rl.world.action.SummonAction;
import com.zygon.rl.world.character.Armor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

/**
 *
 * @author zygon
 */
public class SpawnSystem extends GameSystem {

    // This can theoretically grow unbounded, or maybe it should very slowly
    // age out
    // These should be collapsed..
    private final Set<Location> spawnedLivingLocations = new HashSet<>();
    private final Set<Location> spawnedItemLocations = new HashSet<>();
    private final Random random;
    private final SpawnContext spawnContext;

    public SpawnSystem(GameConfiguration gameConfiguration) {
        super(gameConfiguration);
        this.random = gameConfiguration.getRandom();
        this.spawnContext = gameConfiguration.getSpawnContext();
    }

    @Override
    public GameState apply(GameState state) {
        state = spawnLiving(spawnedLivingLocations, state, state.getWorld().getPlayerLocation());
        state = spawnItem(spawnedItemLocations, state, state.getWorld().getPlayerLocation());
        return state;
    }

    private GameState spawnItem(Set<Location> spawnedLocations, GameState state,
            Location center) {
        double freq = spawnContext.getCityBuildingDistance();

        // rounding should use double, not ints
        //
        Location roundedCenter = Location.create(
                (int) (freq * (Math.round(center.getX() / freq))),
                (int) (freq * (Math.round(center.getY() / freq))));

        CompletionService<GameState> completionService = new ExecutorCompletionService<>(getExecutor());
        int submitCount = 0;

        for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
            for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {

                Location location = Location.create(x, y);

                if (!spawnedLocations.contains(location)) {

                    Callable<GameState> itemSpawnAction = getItemSpawnAction(location, state);
                    completionService.submit(itemSpawnAction);
                    submitCount++;
                    spawnedLocations.add(location);
                }
            }
        }

        for (int i = 0; i < submitCount; i++) {
            try {
                Future<GameState> futureState = completionService.take();
                state = futureState.get();
            } catch (ExecutionException | InterruptedException intr) {
                // TODO: log?
                intr.printStackTrace();
            }
        }

        return state;
    }

    private Callable<GameState> getItemSpawnAction(Location location,
            GameState state) {

        return () -> {
            GameState callableState = state;
            World world = callableState.getWorld();
            WorldRegion region = world.getRegion(location);

            // TODO: other areas
            if (region == WorldRegion.TOWN_RESIDENCE || region == WorldRegion.TOWN_OUTER) {

                List<String> buildingIds = new ArrayList<>(BuildingData.getAllIds());
                Collections.shuffle(buildingIds, World.getNoiseRandom(location));
                BuildingData building = Data.get(buildingIds.get(0));

                if (World.canBuild(callableState.getWorld(), location, building)) {
                    Set<Action> spawnActions = spawnBuildingItems(location, building);

                    for (Action spawnAction : spawnActions) {
                        if (spawnAction.canExecute(callableState)) {
                            callableState = spawnAction.execute(callableState);
                        }
                    }
                }
            }

            return callableState;
        };
    }

    /**
     * Returns a set of actions to spawn building layout Items.
     *
     * @param center
     * @param building
     * @return
     */
    private Set<Action> spawnBuildingItems(Location center, Building building) {

        Set<Action> spawnActions = new HashSet<>();
        Layout layout = building.getLayout();

        int widthFromCenter = layout.getStructure().getWidthFromCenter();
        int heightFromCenter = layout.getStructure().getHeightFromCenter();

        //  "+1" to account for the center tile itself.
        for (int mapY = center.getY() - heightFromCenter, buildingY = 0; mapY < center.getY() + 1 + heightFromCenter; mapY++, buildingY++) {
            for (int mapX = center.getX() - widthFromCenter, buildingX = 0; mapX < center.getX() + 1 + widthFromCenter; mapX++, buildingX++) {

                // Z: testing
                // How to prevent partial-building spawn??????
                if (Location.create(mapX, mapY).equals(center)) {
                    spawnActions.add(new SetItemAction(new Armor(ArmorData.get("torso_tunic_black")), Location.create(mapX, mapY)));
                } else {
                    String itemId = layout.getItems().getId(buildingX, buildingY);
                    if (itemId != null) {
                        Location spawnLocation = Location.create(mapX, mapY);

                        ItemClass item = Data.get(itemId);
                        spawnActions.add(new SetItemAction(new Item(item), spawnLocation));
                    }
                }
            }
        }

        return spawnActions;
    }

    private GameState spawnLiving(Set<Location> spawnedLocations,
            GameState state, Location center) {

        int freq = spawnContext.getLivingSpawnFrequency();

        // round
        Location roundedCenter = Location.create(freq * (Math.round(center.getX() / freq)),
                freq * (Math.round(center.getY() / freq)));

        CompletionService<GameState> completionService = new ExecutorCompletionService<>(getExecutor());
        int submitCount = 0;

        for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
            for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {

                Location location = Location.create(x, y);

                if (!spawnedLocations.contains(location)) {

                    Callable<GameState> livingSpawnAction = getLivingSpawnAction(location, state);
                    completionService.submit(livingSpawnAction);
                    submitCount++;
                    spawnedLocations.add(location);
                }
            }
        }

        for (int i = 0; i < submitCount; i++) {
            try {
                Future<GameState> futureState = completionService.take();
                state = futureState.get();
            } catch (ExecutionException | InterruptedException intr) {
                // TODO: log?
                intr.printStackTrace();
            }
        }

        return state;
    }

    private Callable<GameState> getLivingSpawnAction(Location location,
            GameState state) {

        return () -> {
            GameState callableState = state;
            World world = callableState.getWorld();

            int noiseX = -20 + random.nextInt(40);
            int noiseY = -20 + random.nextInt(40);

            Location rngLocation = Location.create(location.getX() + noiseX, location.getY() + noiseY);
            Terrain terrain = world.getTerrain(location);
            Action summonAction = null;

            if (!terrain.getId().equals(Terrain.Ids.PUDDLE.getId())
                    && !terrain.getId().equals(Terrain.Ids.DEEP_WATER.getId())) {

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

            if (summonAction != null && summonAction.canExecute(callableState)) {
                callableState = summonAction.execute(callableState);
            }

            return callableState;
        };
    }

    private <E> E getRandomSetElement(Set<E> set) {
        return set.stream()
                .skip(random.nextInt(set.size()))
                .findFirst().orElse(null);
    }
}
