package com.zygon.rl.game;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.zygon.rl.data.ItemClass;
import com.zygon.rl.data.buildings.Building;
import com.zygon.rl.data.buildings.BuildingData;
import com.zygon.rl.data.buildings.BuildingLayout;
import com.zygon.rl.data.buildings.Layout;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.data.items.WorldData;
import com.zygon.rl.data.monstergroups.Group;
import com.zygon.rl.data.monstergroups.MonsterGroups;
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

/**
 *
 * @author zygon
 */
public class SpawnContext {

    private static final Logger LOGGER = System.getLogger(SpawnContext.class.getCanonicalName());

    public static record SpawnActionContext(GameState state, Location location,
            Random random) {

    }

    public static enum SpawnContextType {
        Building(center -> {
            int freq = 20;
            Set<Location> spawnLocations = new HashSet<>();

            Location roundedCenter = center.round(freq);

            for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
                for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {
                    spawnLocations.add(Location.create(x, y));
                }
            }
            return spawnLocations;
        },
                SpawnContext::getBuildingSpawnAction),
        Item(center -> {
            int freq = 20;
            Set<Location> spawnLocations = new HashSet<>();

            Location roundedCenter = center.round(freq);

            for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
                for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {
                    spawnLocations.add(Location.create(x, y));
                }
            }
            return spawnLocations;
        },
                SpawnContext::getItemSpawnAction),
        Living(center -> {
            int freq = 100;
            Set<Location> spawnLocations = new HashSet<>();

            Location roundedCenter = center.round(freq);

            for (int y = roundedCenter.getY() + 200, realY = 0; y > roundedCenter.getY() - 200; y -= freq, realY++) {
                for (int x = roundedCenter.getX() - 200, realX = 0; x < roundedCenter.getX() + 200; x += freq, realX++) {
                    spawnLocations.add(Location.create(x, y));
                }
            }
            return spawnLocations;
        },
                SpawnContext::getLivingSpawnAction);

        private final Function<Location, Set<Location>> getSpawnLocFn;
        private final Function<SpawnActionContext, List<Action>> getSpawnActionsFn;

        private SpawnContextType(
                Function<Location, Set<Location>> getSpawnLocFn,
                Function<SpawnActionContext, List<Action>> getSpawnActionsFn) {
            this.getSpawnLocFn = getSpawnLocFn;
            this.getSpawnActionsFn = getSpawnActionsFn;
        }

        public final Function<Location, Set<Location>> getGetSpawnLocFn() {
            return getSpawnLocFn;
        }

        public Function<SpawnActionContext, List<Action>> getGetSpawnActionsFn() {
            return getSpawnActionsFn;
        }
    }

    public static boolean canBuild(World world, Location center,
            Building building) {

        Layout layout = building.getLayout();

        int widthFromCenter = layout.getStructure().getWidthFromCenter();
        int heightFromCenter = layout.getStructure().getHeightFromCenter();

        //  "+1" to account for the center tile itself.
        for (int mapY = center.getY() - heightFromCenter, buildingY = 0; mapY < center.getY() + 1 + heightFromCenter; mapY++, buildingY++) {
            for (int mapX = center.getX() - widthFromCenter, buildingX = 0; mapX < center.getX() + 1 + widthFromCenter; mapX++, buildingX++) {
                Location buildingLocation = Location.create(mapX, mapY);
                WorldRegion region = world.getRegion(buildingLocation);
                if (region != WorldRegion.TOWN_RESIDENCE && region != WorldRegion.TOWN_OUTER) {
                    return false;
                }
            }
        }

        return true;
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
        Random random = actionContext.random();

        List<Action> actions = new ArrayList<>();

        World world = state.getWorld();
        WorldRegion region = world.getRegion(location);

        switch (region) {
            case TOWN_OUTER, TOWN_RESIDENCE -> {

                List<String> buildingIds = new ArrayList<>(BuildingData.getAllIds());
                Collections.shuffle(buildingIds, World.getNoiseRandom(location));
                BuildingData building = Data.get(buildingIds.get(0));

                if (canBuild(state.getWorld(), location, building)) {
                    Set<Action> spawnActions = spawnBuilding(location, building);
                    actions.addAll(spawnActions);
                    actions.add(summonGroup(location, "grp_city", random));
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
        WorldRegion region = world.getRegion(location);

        switch (region) {
            case SHALLOW_WATER, SHORE -> {
                actions.add(summonGroup(rngLocation, "grp_shore", random));
            }
            case SHORT_FIELD, TALL_FIELD -> {
                actions.add(summonGroup(rngLocation, "grp_field", random));
            }
            case FOREST -> {
                actions.add(summonGroup(rngLocation, "grp_forest", random));
            }
        }

        return actions;
    }

    private static Action summonGroup(final Location location,
            final String groupId, final Random random) {
        MonsterGroups monGroup = MonsterGroups.get(groupId);
        Group group = monGroup.getGroup(random);
        String id = group.getId();
        int packSize = group.getPackSize(random);
        return new SummonAction(location, packSize, id, random);
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

        BuildingLayout structure = layout.getStructure();
        int widthFromCenter = structure.getWidthFromCenter();
        int heightFromCenter = structure.getHeightFromCenter();

        //  "+1" to account for the center tile itself.
        for (int mapY = center.getY() - heightFromCenter, buildingY = 0; mapY < center.getY() + 1 + heightFromCenter; mapY++, buildingY++) {
            for (int mapX = center.getX() - widthFromCenter, buildingX = 0; mapX < center.getX() + 1 + widthFromCenter; mapX++, buildingX++) {
                String itemId = layout.getItems().getId(buildingX, buildingY);
                if (itemId != null) {
                    Location spawnLocation = Location.create(mapX, mapY);

                    ItemClass item = Data.get(itemId);
                    spawnActions.add(new SetItemAction(new Item(item), spawnLocation));
                }

                //
                // TODO: doing both is wasteful, right??
                //
                String structureId = structure.getId(buildingX, buildingY);
                if (structureId != null) {
                    Location spawnLocation = Location.create(mapX, mapY);
                    com.zygon.rl.data.items.Building buildingItem = Data.get(structureId);
                    spawnActions.add(new SetItemAction(new Item(buildingItem), spawnLocation));
                }
            }
        }

        return spawnActions;
    }
}
