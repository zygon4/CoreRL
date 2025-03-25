package com.zygon.rl.game;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.zygon.rl.data.ItemClass;
import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.buildings.Building;
import com.zygon.rl.data.buildings.BuildingData;
import com.zygon.rl.data.buildings.Layout;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.ArmorData;
import com.zygon.rl.data.items.WorldData;
import com.zygon.rl.data.monster.Monster;
import com.zygon.rl.data.npc.Npc;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldRegion;
import static com.zygon.rl.world.WorldRegion.SHORE;
import static com.zygon.rl.world.WorldRegion.SHORT_FIELD;
import static com.zygon.rl.world.WorldRegion.TALL_FIELD;
import static com.zygon.rl.world.WorldRegion.TOWN_OUTER;
import static com.zygon.rl.world.WorldRegion.TOWN_RESIDENCE;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.SetItemAction;
import com.zygon.rl.world.action.SummonAction;
import com.zygon.rl.world.character.Armor;

/**
 *
 * @author zygon
 */
public class SpawnContext {

    private static final Logger LOGGER = System.getLogger(SpawnContext.class.getCanonicalName());

    // TODO: this seems to belong somewhere else - Monster maybe?
    private static Map<WorldRegion, Set<String>> creatureIds;

    static {
        // Wish this were more functional..
        Map<WorldRegion, Set<String>> spawnsByRegion = new HashMap<>();
        for (String id : Monster.getAllIds()) {
            Monster mon = Monster.get(id);
            List<String> spawns = mon.getSpawns();
            for (String spawn : spawns) {
                WorldRegion wr = WorldRegion.valueOf(spawn);
                Set<String> creatureIds = spawnsByRegion.computeIfAbsent(wr, k -> new HashSet<>());
                creatureIds.add(id);
            }
        }

        creatureIds = Collections.unmodifiableMap(spawnsByRegion);
    }

    // auto-format of records is jank
    public static record SpawnActionContext(GameState state, Location location,
            Random random) {

    }

    ;

    public static enum SpawnContextType {
        Building(20,
                center -> {
                    int freq = 20;
                    Set<Location> spawnLocations = new HashSet<>();

                    Location roundedCenter = Location.create((int) (freq * (Math.round(center.getX() / freq))),
                            (int) (freq * (Math.round(center.getY() / freq))));

                    for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
                        for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {
                            spawnLocations.add(Location.create(x, y));
                        }
                    }
                    return spawnLocations;
                },
                SpawnContext::getBuildingSpawnAction),
        Item(5,
                center -> {
                    int freq = 20;
                    Set<Location> spawnLocations = new HashSet<>();

                    Location roundedCenter = Location.create((int) (freq * (Math.round(center.getX() / freq))),
                            (int) (freq * (Math.round(center.getY() / freq))));

                    for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
                        for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {
                            spawnLocations.add(Location.create(x, y));
                        }
                    }
                    return spawnLocations;
                },
                SpawnContext::getItemSpawnAction),
        Living(30,
                center -> {
                    int freq = 50;
                    Set<Location> spawnLocations = new HashSet<>();

                    Location roundedCenter = Location.create((int) (freq * (Math.round(center.getX() / freq))),
                            (int) (freq * (Math.round(center.getY() / freq))));

                    for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
                        for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {
                            spawnLocations.add(Location.create(x, y));
                        }
                    }
                    return spawnLocations;
                },
                SpawnContext::getLivingSpawnAction);

        private final int spawnFrequency;
        private final Function<Location, Set<Location>> getSpawnLocFn;
        private final Function<SpawnActionContext, List<Action>> getSpawnActionsFn;

        private SpawnContextType(int spawnFrequency,
                Function<Location, Set<Location>> getSpawnLocFn,
                Function<SpawnActionContext, List<Action>> getSpawnActionsFn) {
            this.spawnFrequency = spawnFrequency;
            this.getSpawnLocFn = getSpawnLocFn;
            this.getSpawnActionsFn = getSpawnActionsFn;
        }

        public final int spawnFrequency() {
            return spawnFrequency;
        }

        public final Function<Location, Set<Location>> getGetSpawnLocFn() {
            return getSpawnLocFn;
        }

        public Function<SpawnActionContext, List<Action>> getGetSpawnActionsFn() {
            return getSpawnActionsFn;
        }
    }

    private static Location getRandomLocation(Location location,
            int maxRadius, Random rand) {
        int halfRadius = maxRadius / 2;
        return Location.create(
                (location.getX() - halfRadius) + rand.nextInt(maxRadius),
                (location.getY() - halfRadius) + rand.nextInt(maxRadius));
    }

    private static List<Action> getBuildingSpawnAction(
            SpawnActionContext actionContext) {

        GameState state = actionContext.state();
        Location location = actionContext.location();

        List<Action> actions = new ArrayList<>();

        World world = state.getWorld();
        WorldRegion region = world.getRegion(location);

        switch (region) {
            case TOWN_OUTER, TOWN_RESIDENCE -> {

                List<String> buildingIds = new ArrayList<>(BuildingData.getAllIds());
                Collections.shuffle(buildingIds, World.getNoiseRandom(location));
                BuildingData building = Data.get(buildingIds.get(0));

                if (World.canBuild(state.getWorld(), location, building)) {
                    Set<Action> spawnActions = spawnBuilding(location, building);
                    actions.addAll(spawnActions);
                }
            }
        }

        return actions;
    }

    private static List<Action> getItemSpawnAction(
            SpawnActionContext actionContext) {

        GameState state = actionContext.state();
        Location location = actionContext.location();
        Random random = actionContext.random();

        List<Action> actions = new ArrayList<>();

        World world = state.getWorld();
        WorldRegion region = world.getRegion(location);

        switch (region) {
            case TOWN_OUTER, TOWN_RESIDENCE -> {
                WorldData rock = Data.get("item_rock");
                Item i = new Item(rock, rock.getWeight());
                Action spawnItem = new SetItemAction(i,
                        getRandomLocation(location, 5, random));
                actions.add(spawnItem);
            }
            case SHORE -> {
                WorldData stone = Data.get("item_stone");
                Item i = new Item(stone, stone.getWeight());
                Action spawnItem = new SetItemAction(i,
                        getRandomLocation(location, 5, random));
                actions.add(spawnItem);
            }
            case SHORT_FIELD, TALL_FIELD -> {
                WorldData.WorldItems[] values = WorldData.WorldItems.values();
                final int rand = random.nextInt(values.length);
                WorldData item = Data.get(values[rand].getId());
                Item i = new Item(item, item.getWeight());
                Action spawnItem = new SetItemAction(i,
                        getRandomLocation(location, 5, random));
                actions.add(spawnItem);
            }
        }

        return actions;
    }

    public static List<Action> getLivingSpawnAction(
            SpawnActionContext actionContext) {

        GameState state = actionContext.state();
        Location location = actionContext.location();
        Random random = actionContext.random();

        List<Action> actions = new ArrayList<>();

        GameState callableState = state;
        World world = callableState.getWorld();

        int noiseX = -20 + random.nextInt(40);
        int noiseY = -20 + random.nextInt(40);

        Location rngLocation = Location.create(location.getX() + noiseX, location.getY() + noiseY);
        Terrain terrain = world.getTerrain(location);
        WorldRegion region = world.getRegion(location);
        Set<String> regionCreatureIds = creatureIds.get(region);

        Action summonAction = null;

        if (!terrain.getId().equals(Terrain.Ids.PUDDLE.getId())
                && !terrain.getId().equals(Terrain.Ids.DEEP_WATER.getId())) {

            String creatureId = null;

            if (random.nextDouble() > .10) {
                creatureId = getRandomSetElement(regionCreatureIds, random);
            } else {
                // TODO: set names on resulting spawns
                // TODO: equipment/items on NPCs
                creatureId = getRandomSetElement(Npc.getAllIds(), random);
            }

            summonAction = new SummonAction(rngLocation, random.nextInt(4), creatureId, random);
            actions.add(summonAction);
        }

        return actions;
    }

    private static <E> E getRandomSetElement(Set<E> set, Random random) {
        return set.stream()
                .skip(random.nextInt(set.size()))
                .findFirst().orElse(null);
    }

    /**
     * Returns a set of actions to spawn building layout Items.
     *
     * @param center
     * @param building
     * @return
     */
    private static Set<Action> spawnBuilding(Location center, Building building) {

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
}
