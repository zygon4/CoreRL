package com.zygon.rl.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Region
 *
 */
public final class Region {

    static final Location DEFAULT_MIN_LOCATION = Location.create(
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    static final Location DEFAULT_MAX_LOCATION = Location.create(
            Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    private final Map<Location, List<Entity>> entitiesByLocation;
    private final Map<Entity, Set<Location>> locationsByEntity;

    private final Location minValues;
    private final Location maxValues;

    private Region(Map<Location, List<Entity>> entitiesByLocation,
            Map<Entity, Set<Location>> locationByentity,
            Location minValues, Location maxValues) {

        // TBD: mutable, ick
        this.entitiesByLocation = entitiesByLocation;
        this.locationsByEntity = locationByentity;

        this.minValues = minValues;
        this.maxValues = maxValues;
    }

    public Region() {
        this(new HashMap<>(), new HashMap<>(), DEFAULT_MIN_LOCATION, DEFAULT_MAX_LOCATION);
    }

    public Region move(Entity entity, Location destination) {
        Set<Location> entityLocations = find(entity);

        // could throw state exception instead
        if (entityLocations != null && !entityLocations.isEmpty()) {
            Region added = null;
            Location newMin = null;
            Location newMax = null;

            for (Location loc : entityLocations) {
                Region removed = remove(entity, loc);

                added = removed.add(entity, destination);
                newMin = getMin(minValues, destination);
                newMax = getMax(maxValues, destination);
            }

            return new Region(added.entitiesByLocation, added.locationsByEntity, newMin, newMax);

        } else {
            return this;
        }
    }

    // No longer immutable :(
    public Region add(Map<Location, List<Entity>> newEntitiesByLocation) {
        // Add to loc -> entity mapping
        Map<Location, List<Entity>> entsByLoc = entitiesByLocation;

        for (Map.Entry<Location, List<Entity>> entry : newEntitiesByLocation.entrySet()) {
            Location location = entry.getKey();
            List<Entity> ents = entsByLoc.get(location);
            if (ents == null) {
                ents = new ArrayList<>();
                entsByLoc.put(location, ents);
            }
            ents.addAll(entry.getValue());
        }

        // Add to entity -> loc mapping
        Map<Entity, Set<Location>> locByEntity = locationsByEntity;

        Location newMin = null;
        Location newMax = null;

        for (Map.Entry<Location, List<Entity>> entry : newEntitiesByLocation.entrySet()) {
            Location location = entry.getKey();
            for (Entity entity : entry.getValue()) {
                Set<Location> entityLocs = locByEntity.get(entity);
                if (entityLocs == null) {
                    entityLocs = new HashSet<>();
                    locByEntity.put(entity, entityLocs);
                }
                entityLocs.add(location);
            }

            newMin = getMin(newMin == null ? minValues : newMin, location);
            newMax = getMax(newMax == null ? maxValues : newMax, location);
        }

        return new Region(entsByLoc, locByEntity, newMin, newMax);
    }

    public Region add(List<Entity> entities, Location location) {
        return add(Collections.singletonMap(location, entities));
    }

    public Region add(Entity entity, Location location) {
        return add(Collections.singletonList(entity), location);
    }

    public boolean contains(Location location) {
        return location.getX() >= minValues.getX() && location.getX() <= maxValues.getX()
                && location.getY() >= minValues.getY() && location.getY() <= maxValues.getY();
    }

    public List<Entity> get(Location location) {
        List<Entity> entities = entitiesByLocation.get(location);
        return entities == null ? Collections.emptyList() : Collections.unmodifiableList(entities);
    }

    public Entity get(Location location, int layer) {
        List<Entity> entities = entitiesByLocation.get(location);
        return entities == null ? null : entities.get(layer);
    }

    public int getHeight() {
        return maxValues.getY() - minValues.getY() + 1;
    }

    public int getWidth() {
        return maxValues.getX() - minValues.getX() + 1;
    }

    public Set<Location> find(String entityName) {
        Set<Location> finds = new HashSet<>();

        for (Map.Entry<Entity, Set<Location>> entry : locationsByEntity.entrySet()) {
            if (entry.getKey().getName().equals(entityName)) {
                finds.addAll(entry.getValue());
            }
        }

        return finds;
    }

    public Set<Location> find(Entity entity) {
        Set<Location> locations = locationsByEntity.get(entity);
        return locations != null
                ? Collections.unmodifiableSet(locations) : Collections.emptySet();
    }

    public Region remove(Entity entity, Location location) {
        // Remove from loc -> entity mapping
        Map<Location, List<Entity>> entsByLoc = new HashMap<>(entitiesByLocation);
        List<Entity> ents = entsByLoc.get(location);

        if (ents != null) {
            ents = ents.stream()
                    .filter(ent -> !ent.equals(entity))
                    .collect(Collectors.toList());

            entsByLoc.put(location, ents);
        }

        // Remove from entity -> loc mapping
        Map<Entity, Set<Location>> locByEntity = new HashMap<>(locationsByEntity);
        Set<Location> locationsForEntity = locByEntity.get(entity);
        if (locationsForEntity != null) {
            locationsForEntity.remove(location);
            if (locationsForEntity.isEmpty()) {
                locByEntity.remove(entity);
            }
        }

        Location newMin = getMin(minValues, location);
        Location newMax = getMax(maxValues, location);

        return new Region(entsByLoc, locByEntity, newMin, newMax);
    }

    public Location getMaxValues() {
        return maxValues;
    }

    public Location getMinValues() {
        return minValues;
    }

    @Override
    public String toString() {
        return getMinValues() + ":" + getMaxValues();
    }

    static Location getMin(Location minValues, Location incoming) {
        final int incomingX = incoming.getX();
        final int incomingY = incoming.getY();
        final int incomingZ = incoming.getZ();

        int newXMin = minValues.getX();
        int newYMin = minValues.getY();
        int newZMin = minValues.getZ();

        if (incomingX < minValues.getX()) {
            newXMin = incomingX;
        }

        if (incomingY < minValues.getY()) {
            newYMin = incomingY;
        }

        if (incomingZ < minValues.getZ()) {
            newZMin = incomingZ;
        }

        return Location.create(newXMin, newYMin, newZMin);
    }

    static Location getMax(Location maxValues, Location location) {
        final int incomingX = location.getX();
        final int incomingY = location.getY();
        final int incomingZ = location.getZ();

        int newXMax = maxValues.getX();
        int newYMax = maxValues.getY();
        int newZMax = maxValues.getZ();

        if (incomingX > maxValues.getX()) {
            newXMax = incomingX;
        }

        if (incomingY > maxValues.getY()) {
            newYMax = incomingY;
        }

        if (incomingZ > maxValues.getZ()) {
            newZMax = incomingZ;
        }

        return Location.create(newXMax, newYMax, newZMax);
    }
}
