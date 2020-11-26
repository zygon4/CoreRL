package com.zygon.rl.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class is trouble! Rendering a stitched view is complex.
 *
 * @author zygon
 */
public class Regions {

    public static final int REGION_EDGE_SIZE = 20;

    // Bottom left oriented
    private final Map<Location, Region> regionsByLocation;

    private final Location minValues;
    private final Location maxValues;

    private Regions(Map<Location, Region> regionsByLocation, Location minValues, Location maxValues) {
        this.regionsByLocation = regionsByLocation != null
                ? Collections.unmodifiableMap(regionsByLocation)
                : Collections.emptyMap();
        this.minValues = minValues;
        this.maxValues = maxValues;
    }

    public static Regions create() {
        return new Regions(null, Region.DEFAULT_MIN_LOCATION, Region.DEFAULT_MAX_LOCATION);
    }

    public Regions add(Entity entity, Location location) {
        Map<Location, Region> regions = new HashMap<>(regionsByLocation);

        Region withAdd = getRegion(location).add(entity, location);
        add(withAdd, regions);

        return new Regions(regions, minValues, maxValues);
    }

    public Regions add(Region region) {

        Map<Location, Region> regions = new HashMap<>(regionsByLocation);
        add(region, regions);

        Location newMin = null;
        Location newMax = null;

        for (Region loc : regions.values()) {
            newMin = newMin == null ? loc.getMinValues()
                    : Region.getMin(newMin, loc.getMinValues());
            newMax = newMax == null ? loc.getMaxValues()
                    : Region.getMax(newMax, loc.getMaxValues());
        }

        return new Regions(regions, newMin, newMax);
    }

    public Set<Location> find(String entityName, Function<Location, Boolean> entityFilter) {
        Set<Location> finds = new HashSet<>();
        for (Region region : regionsByLocation.values()) {
            Set<Location> regionFinds = region.find(entityName);
            for (Location regionFind : regionFinds) {
                if (entityFilter.apply(regionFind)) {
                    finds.add(regionFind);
                }
            }
        }
        return finds;
    }

    public Set<Location> find(String entityName) {
        return find(entityName, loc -> true);
    }

    public Set<Location> find(Entity entity, Function<Location, Boolean> entityFilter) {
        return find(entity.getName(), entityFilter);
    }

    public Set<Location> find(Entity entity) {
        return find(entity.getName());
    }

    public List<Entity> get(Location location) {
        return getRegion(location).get(location);
    }

    public Location getMaxValues() {
        return maxValues;
    }

    public Location getMinValues() {
        return minValues;
    }

    public Region getRegion(Location location) {
        // TODO: could have this indexed, would be faster
        for (Region region : regionsByLocation.values()) {
            if (region.contains(location)) {
                return region;
            }
        }

        return null;
    }

    // Note: removed the streaming methods here for performance reasons
    public Regions move(Entity entity, Location destination) {

        Set<Location> sourceLocations = new HashSet<>();
        for (Map.Entry<Location, Region> entry : regionsByLocation.entrySet()) {
            Set<Location> locations = entry.getValue().find(entity);
            sourceLocations.addAll(locations);
        }

        Map<Location, Region> sourceRegionsByLocation = new HashMap<>();
        for (Location sourceLocation : sourceLocations) {
            sourceRegionsByLocation.put(sourceLocation, getRegion(sourceLocation));
        }

        Region targetRegion = null;
        for (Map.Entry<Location, Region> entry : regionsByLocation.entrySet()) {
            Region region = entry.getValue();
            if (region.contains(destination)) {
                targetRegion = region;
                break;
            }
        }

        Map<Location, Region> regions = new HashMap<>(regionsByLocation);

        for (Location l : sourceLocations) {
            Region sourceRegion = sourceRegionsByLocation.get(l);

            // actual object reference equality
            if (sourceRegion == targetRegion) {
                Region singleRegionMove = sourceRegion.move(entity, destination);
                add(singleRegionMove, regions);
            } else {
                sourceRegion = sourceRegion.remove(entity, l);
                add(sourceRegion, regions);

                targetRegion = targetRegion.add(entity, destination);
                add(targetRegion, regions);
            }
        }

        return new Regions(regions, minValues, maxValues);
    }

    public Regions remove(Entity entity, Location location) {
        Map<Location, Region> regions = new HashMap<>(regionsByLocation);

        Region withRemoval = getRegion(location).remove(entity, location);
        add(withRemoval, regions);

        return new Regions(regions, minValues, maxValues);
    }

    private void add(Map<Location, List<Entity>> outEntitiesByLocation,
            Region toRegion, Region fromRegion, int startX, int startY, int stripWidth, int stripHeight) {

        for (int y = startY; y < startY + stripHeight; y++) {
            for (int x = startX; x < startX + stripWidth; x++) {
                Location l = Location.create(x, y);

                List<Entity> entities = fromRegion.get(l);
                toRegion = toRegion.add(entities, l);
                outEntitiesByLocation.put(l, entities);
            }
        }
    }

    private void add(Region region, Map<Location, Region> regions) {
        Location minLocation = region.getMinValues();
        regions.put(minLocation, region);
    }
}
