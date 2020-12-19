package com.zygon.rl.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class World {

    private final Map<Location, List<Entity>> entitiesByLocation;
    private final Map<Entity, Set<Location>> locationsByEntity;

    private World(Map<Location, List<Entity>> entitiesByLocation,
            Map<Entity, Set<Location>> locationByentity) {
        this.entitiesByLocation = entitiesByLocation;
        this.locationsByEntity = locationByentity;
    }

    public World() {
        this(new HashMap<>(), new HashMap<>());
    }

    public Collection<Entity> queryAll(WorldQuery query) {
        // TODO:
        return Collections.emptySet();
    }

    public Entity query(WorldQuery query) {
        return queryAll(query).iterator().next();
    }

    public World move(Entity entity, Location destination) {
        Set<Location> entityLocations = find(entity);

        // could throw state exception instead
        if (entityLocations != null && !entityLocations.isEmpty()) {
            World added = null;

            for (Location loc : entityLocations) {
                World removed = remove(entity, loc);

                added = removed.add(entity, destination);
            }

            return new World(added.entitiesByLocation, added.locationsByEntity);
        } else {
            return this;
        }
    }

    // No longer immutable :(
    public World add(Map<Location, List<Entity>> newEntitiesByLocation) {
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
        }

        return new World(entsByLoc, locByEntity);
    }

    public World add(List<Entity> entities, Location location) {
        return add(Collections.singletonMap(location, entities));
    }

    public World add(Entity entity, Location location) {
        return add(Collections.singletonList(entity), location);
    }

    public List<Entity> get(Location location) {
        List<Entity> entities = entitiesByLocation.get(location);
        return entities == null ? Collections.emptyList() : Collections.unmodifiableList(entities);
    }

    public Entity get(Location location, int layer) {
        List<Entity> entities = entitiesByLocation.get(location);
        return entities == null ? null : entities.get(layer);
    }

    // TODO: find should have more query parameters, use the query request
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

    public World remove(Entity entity, Location location) {
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

        return new World(entsByLoc, locByEntity);
    }
}
