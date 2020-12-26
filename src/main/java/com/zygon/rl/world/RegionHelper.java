/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.world;

import com.zygon.rl.util.rng.family.FamilyTreeGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class RegionHelper {

    private static final double MONSTER_RAND_WEIGHT = .02;
    private static final double PLAYER_RAND_WEIGHT = .01;

    // TODO: for all of these adjustable weights, put them into JSON!!
    public Region generateForestRegion(Location start, int maxX, int maxY) {

        // TODO: make static OR add noise, ditch the numberNames
        Map<Double, Entity> layerZeroEntitiesByWeight = new HashMap<>();
        layerZeroEntitiesByWeight.put(0.75, Entities.GRASS);
        layerZeroEntitiesByWeight.put(0.20, Entities.DIRT);
        layerZeroEntitiesByWeight.put(0.05, Entities.PUDDLE);

        Map<Double, Entity> layerOneEntitiesByWeight = new HashMap<>();

        layerOneEntitiesByWeight.put(0.93, null);
        layerOneEntitiesByWeight.put(0.06, Entities.TREE);
        layerOneEntitiesByWeight.put(PLAYER_RAND_WEIGHT, Entities.PLAYER);

        List<Map<Double, Entity>> entityLayers = new ArrayList<>();
        entityLayers.add(layerZeroEntitiesByWeight);
        entityLayers.add(layerOneEntitiesByWeight);
        return generateRegion(start, maxX, maxY, entityLayers, false);
    }

    public Region generateFieldRegion(Location start, int maxX, int maxY) {

        // TODO: make static OR add noise, ditch the numberNames
        Map<Double, Entity> layerZeroEntitiesByWeight = new HashMap<>();
        layerZeroEntitiesByWeight.put(0.75, Entities.GRASS);
        layerZeroEntitiesByWeight.put(0.10, Entities.TALL_GRASS);
        layerZeroEntitiesByWeight.put(0.10, Entities.DIRT);
        layerZeroEntitiesByWeight.put(0.05, Entities.PUDDLE);

        Map<Double, Entity> layerOneEntitiesByWeight = new HashMap<>();

        layerOneEntitiesByWeight.put(0.97, null);
        layerOneEntitiesByWeight.put(0.02, Entities.TREE);
        layerOneEntitiesByWeight.put(PLAYER_RAND_WEIGHT, Entities.PLAYER);

        List<Map<Double, Entity>> entityLayers = new ArrayList<>();
        entityLayers.add(layerZeroEntitiesByWeight);
        entityLayers.add(layerOneEntitiesByWeight);
        return generateRegion(start, maxX, maxY, entityLayers, false);
    }

    public Region generateSwampRegion(Location start, int maxX, int maxY) {

        // TODO: make static OR add noise, ditch the numberNames
        Map<Double, Entity> layerZeroEntitiesByWeight = new HashMap<>();
        layerZeroEntitiesByWeight.put(0.35, Entities.GRASS);
        layerZeroEntitiesByWeight.put(0.25, Entities.DIRT);
        layerZeroEntitiesByWeight.put(0.40, Entities.PUDDLE);

        Map<Double, Entity> layerOneEntitiesByWeight = new HashMap<>();

        layerOneEntitiesByWeight.put(0.94, null);
        layerOneEntitiesByWeight.put(0.05, Entities.TREE);
        layerOneEntitiesByWeight.put(PLAYER_RAND_WEIGHT, Entities.PLAYER);

        List<Map<Double, Entity>> entityLayers = new ArrayList<>();
        entityLayers.add(layerZeroEntitiesByWeight);
        entityLayers.add(layerOneEntitiesByWeight);
        return generateRegion(start, maxX, maxY, entityLayers, false);
    }

    public Region generateCity(Location start, int maxX, int maxY) {

        // TODO: make static OR add noise, ditch the numberNames
        Map<Double, Entity> layerZeroEntitiesByWeight = new HashMap<>();
        layerZeroEntitiesByWeight.put(0.89, Entities.GRASS);
        layerZeroEntitiesByWeight.put(0.06, Entities.DIRT);
        layerZeroEntitiesByWeight.put(0.05, Entities.PUDDLE);

        Map<Double, Entity> layerOneEntitiesByWeight = new HashMap<>();

        layerOneEntitiesByWeight.put(0.90, null);
        layerOneEntitiesByWeight.put(0.07, Entities.TREE);
        layerOneEntitiesByWeight.put(MONSTER_RAND_WEIGHT, Entities.MONSTER);
        layerOneEntitiesByWeight.put(PLAYER_RAND_WEIGHT, Entities.PLAYER);

        List<Map<Double, Entity>> entityLayers = new ArrayList<>();
        entityLayers.add(layerZeroEntitiesByWeight);
        entityLayers.add(layerOneEntitiesByWeight);
        return generateRegion(start, maxX, maxY, entityLayers, true);
    }

    private Region smooth(Region region, Location start, int maxX, int maxY,
            List<Location> availablePlayerLocations,
            Map<Location, Set<Entity>> cityEntitiesByLocation,
            List<Map<Double, Entity>> entityWeightByLayers) {

        Region newRegion = new Region();

        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {

                // Don't use this to add to the region, it's always zero based.
                Location location = Location.create(x, y);

                // Location in grid is zero based. So create an offset location
                // for adding to the actual region.
                Location regionOffsetLoc = Location.create(
                        location.getX() + start.getX(),
                        location.getY() + start.getY());

                Set<Entity> entities = cityEntitiesByLocation.get(location);
                if (entities != null) {
                    for (Entity entity : entities) {
                        newRegion = newRegion.add(entity, regionOffsetLoc);
                    }
                } else {
                    final Region finalOrigRegion = region;

                    // Skew the random weights by nearby locations to have
                    // a smoothing concept
                    // Note: searching the original region because it acts like a diagram
                    // but we want to generate a new one.
                    Set<Location> neighbors = location.getNeighbors();
                    Map<String, List<Entity>> neighborEntitiesByName = neighbors.stream()
                            .map(l -> finalOrigRegion.get(l, 0))
                            .filter(Objects::nonNull)
                            .collect(Collectors.groupingBy(Entity::getName));

                    for (int layerIdx = 0; layerIdx < entityWeightByLayers.size(); layerIdx++) {

                        RandomCollection<Entity> smoothedRandomCollection = new RandomCollection<>();

                        entityWeightByLayers.get(layerIdx).forEach((k, v) -> {
                            double weight = k;
                            // value (ie weight) as null is used to signify: do nothing
                            if (v != null) {
                                List<Entity> ents = neighborEntitiesByName.get(v.getName());
                                weight = ents != null ? k * ents.size() : k;
                            }
                            smoothedRandomCollection.add(weight, v);
                        });

                        Entity entity = smoothedRandomCollection.next();

                        if (entity != null) {
                            // yucky special case
                            if (entity.equals(Entities.MONSTER)) {
                                entity = Entities.createMonster(
                                        FamilyTreeGenerator.create().getName().toString()).build();
                            }
                            newRegion = newRegion.add(entity, regionOffsetLoc);

                            // check if players can't visit
                            if (entity.getAttribute(CommonAttributes.IMPASSABLE.name()) == null) {
                                availablePlayerLocations.add(regionOffsetLoc);
                            } else {
                                availablePlayerLocations.remove(regionOffsetLoc);
                            }
                        }
                    } // FOR layerIdx
                }
            }
        }

        return newRegion;
    }

    private Region generateRegion(Location start, int maxX, int maxY,
            List<Map<Double, Entity>> entityLayers, boolean fillCity) {

        Region region = new Region();

        // TODO: fix this "city" concept, it's an invasive concept here
        Map<Location, Set<Entity>> cityEntitiesByLocation = Collections.emptyMap();

        // TODO:
//        if (fillCity) {
//            // TODO: random check for "walled city"
//            Random rand = new Random();
//            Grid grid = new Grid(maxX, maxY);
//            CityGenerator cityGenerator = new CityGenerator(rand, rand.nextBoolean());
//            cityGenerator.setMinRoomSize(3);
//            int roomGenAttempts = (grid.getWidth() / cityGenerator.getMaxRoomSize())
//                    * (grid.getHeight() / cityGenerator.getMaxRoomSize());
//            cityGenerator.setRoomGenerationAttempts(roomGenAttempts * 2);
//            cityGenerator.generate(grid);
//            cityEntitiesByLocation = cityGenerator.getRoomsEntitiesByLocation();
//        }
        List<Location> availablePlayerLocations = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            availablePlayerLocations.clear();

            region = smooth(region, start, maxX, maxY,
                    availablePlayerLocations, cityEntitiesByLocation, entityLayers);
        }

        return region;
    }

    // Main method to generate random terrain regions
    public Region generateRegion(Location start, int maxX, int maxY, boolean withPlayer) {

        RandomCollection<Terrain> randomTerrain = new RandomCollection<>();

        randomTerrain.add(0.05, Terrain.CITY);
        randomTerrain.add(0.65, Terrain.FIELD);
        randomTerrain.add(0.20, Terrain.FOREST);
        randomTerrain.add(0.10, Terrain.SWAMP);

        Region region = null;

        switch (randomTerrain.next()) {
            case CITY:
                region = generateCity(start, maxX, maxY);
                break;
            case FIELD:
                region = generateFieldRegion(start, maxX, maxY);
                break;
            case FOREST:
                region = generateForestRegion(start, maxX, maxY);
                break;
            case SWAMP:
                region = generateSwampRegion(start, maxX, maxY);
                break;
        }

        Objects.requireNonNull(region);

        Set<Location> removePlayers = new HashSet<>();
        if (withPlayer) {
            // TBD: this is a mild hack, removing the extra player entities after
            // all the initial regions are generated.
            removePlayers.addAll(region.find(Entities.PLAYER).stream()
                    .skip(1)
                    .collect(Collectors.toSet()));
        } else {
            removePlayers.addAll(region.find(Entities.PLAYER).stream()
                    .collect(Collectors.toSet()));
        }
        for (Location removePlayerLoc : removePlayers) {
            region = region.remove(Entities.PLAYER, removePlayerLoc);
        }
        if (withPlayer) {
            if (region.find(Entities.PLAYER).size() != 1) {
                throw new RuntimeException("Number of players is wrong");
            }
        } else {
            if (!region.find(Entities.PLAYER).isEmpty()) {
                throw new RuntimeException("Player generated");
            }
        }

        return Objects.requireNonNull(region);
    }

    // TODO: city generation: need to bring back the library
//    private static class CityGenerator extends DungeonGenerator {
//
//        // Not immutable for performance
//        private final Map<Location, Set<Entity>> roomsEntitiesByLocation = new HashMap<>();
//        private final Random rand;
//        private final boolean border;
//
//        public CityGenerator(Random rand, boolean border) {
//            this.rand = rand != null ? rand : new Random();
//            this.border = border;
//        }
//
//        // Includes walls
//        private Map<Location, Set<Entity>> getRoomsEntitiesByLocation() {
//            return roomsEntitiesByLocation;
//        }
//
//        // From AbstractRoomGenerator
//        @Override
//        protected void carveRoom(Grid grid, Room room, float value) {
//            super.carveRoom(grid, room, value);
//
//            for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
//                for (int x = room.getX(); x < room.getX() + room.getWidth(); x++) {
//                    Location location = Location.create(x, y);
//
//                    // TODO: also use weighted random
//                    if (room.isBorder(x, y)) {
//                        Set<Entity> entities = new HashSet<>();
//                        entities.add(Entities.DIRT);
//
//                        double random = rand.nextDouble();
//                        if (random > 0.95) {
//                            entities.add(Entities.createDoor());
//                        } else if (random > .90) {
//                            entities.add(Entities.createWindow());
//                        } else {
//                            entities.add(Entities.WALL);
//                        }
//
//                        // TODO: random door (at most 1 or 2, at least 1)
//                        roomsEntitiesByLocation.put(location, entities);
//                    } else {
//                        Set<Entity> entities = new HashSet<>();
//                        entities.add(Entities.FLOOR);
//                        roomsEntitiesByLocation.put(location, entities);
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void generate(Grid grid) {
//
//            if (border) {
//                int gridX = grid.getWidth();
//                int gridY = grid.getHeight();
//
//                Set<Entity> entities = new HashSet<>();
//                entities.add(Entities.WALL);
//
//                for (int y = 0; y < gridY; y++) {
//                    if (y % 2 == 0) {
//                        Location locationXmin = Location.create(0, y);
//                        Location locationXMax = Location.create(gridX - 1, y);
//                        roomsEntitiesByLocation.put(locationXmin, entities);
//                        roomsEntitiesByLocation.put(locationXMax, entities);
//                    }
//                }
//
//                for (int x = 0; x < gridX; x++) {
//                    if (x % 2 == 0) {
//                        Location locationYmin = Location.create(x, 0);
//                        Location locationYMax = Location.create(x, gridY - 1);
//                        roomsEntitiesByLocation.put(locationYmin, entities);
//                        roomsEntitiesByLocation.put(locationYMax, entities);
//                    }
//                }
//            }
//
//            super.generate(grid);
//        }
//
//        @Override
//        protected void spawnCorridors(Grid grid) {
//            // No op
//        }
//
//        @Override
//        protected void joinRegions(Grid grid) {
//            // No op
//        }
//
//        @Override
//        protected void removeDeadEnds(Grid grid) {
//            // No op
//        }
//    }
    private final class RandomCollection<E> {

        // Ugh, this collapses on the weight so you can't have multiple
        // terrains with the exact same weight.
        // TODO: fix this
        private final NavigableMap<Double, E> map = new TreeMap<>();
        private final Random random;
        private double total = 0;

        public RandomCollection() {
            this(new Random());
        }

        public RandomCollection(Random random) {
            this.random = random;
        }

        public RandomCollection<E> add(double weight, E result) {
            if (weight <= 0) {
                return this;
            }

            if (map.containsKey(weight)) {
                return add(weight + random.nextDouble(), result);
            }

            total += weight;
            map.put(total, result);
            return this;
        }

        public E next() {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        }
    }
}
