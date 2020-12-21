package com.zygon.rl.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mutable state here..
 *
 * @author zygon
 */
public class EntityManager {

    public static class Query {

        private final Map<String, String> attributes;
        private final Location location;

        private Query(Builder builder) {
            this.attributes = Collections.unmodifiableMap(builder.attributes);
            this.location = builder.location;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public Location getLocation() {
            return location;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private Map<String, String> attributes = new HashMap<>();
            private Location location;

            /**
             * Any entity with this attribute will be returned regardless of the
             * value.
             *
             * @param name
             * @return
             */
            public Builder addAttribute(String name) {
                attributes.put(name, null);
                return this;
            }

            /**
             * Any entity with this attribute=value will be returned.
             *
             * @param name
             * @param value
             * @return
             */
            public Builder addAttribute(String name, String value) {
                attributes.put(name, value);
                return this;
            }

            /**
             * Returns entities at the given location
             *
             * @param location
             * @return
             */
            public Builder setLocation(Location location) {
                this.location = location;
                return this;
            }

            public Query build() {
                return new Query(this);
            }
        }
    }

    // TBD: caching if needed
    // TBD: two maps could be big?? Also double-storing uuids which isn't great.
    private final Map<UUID, Entity> entitiesByUuid = new HashMap<>();
    private final Map<String, Set<UUID>> entityUuidByAttribyuteName = new HashMap<>();

    // Basic CRUD for entities..
    public void delete(UUID uuid) {
        Entity existing = get(uuid);
        // remove from uuid map
        entitiesByUuid.remove(uuid);

        // remove from attribute maps
        for (String attrName : existing.getAttributeNames()) {
            entityUuidByAttribyuteName.get(attrName).remove(uuid);
        }
    }

    public Set<Entity> findAll(Query query) {

        Set<Entity> entities = new HashSet<>();

        for (var attribute : query.getAttributes().entrySet()) {
            String attrName = attribute.getKey();
            String attrValue = attribute.getValue();

            Set<UUID> entitiesWithAttr = entityUuidByAttribyuteName.get(attrName);

            if (attrValue != null) {
                // Get entities with attr=val
                entities.addAll(entitiesWithAttr.stream()
                        .map(this::get)
                        .filter(ent -> ent.getAttribute(attrName).getValue().equals(attrValue))
                        .collect(Collectors.toSet()));
            } else {
                // Get the entities that have this attribute e.g. "get all cursed"
                entities.addAll(entitiesWithAttr.stream()
                        .map(this::get)
                        .collect(Collectors.toSet()));
            }
        }

        // Note: this is a "query for any" implementation.
        // It would be better to have a query that only returns entries that
        // satisfy all queries.
        //
        //
        // Query for location
        // TBD: consider storing by location if slow
        // The original Region code had a location cache
        Location queryLocation = query.getLocation();
        if (queryLocation != null) {
            entities.addAll(entitiesByUuid.values().stream()
                    .filter(ent -> ent.getLocation().equals(queryLocation))
                    .collect(Collectors.toSet()));
        }

        return entities;
    }

    public Entity find(Query query) {
        return findAll(query).iterator().next();
    }

    public Entity get(UUID uuid) {
        return entitiesByUuid.get(uuid);
    }

    public Entity save(Entity entity) {
        entitiesByUuid.put(entity.getId(), entity);
        for (String attrName : entity.getAttributeNames()) {
            Set<UUID> entityUuids = entityUuidByAttribyuteName.get(attrName);
            if (entityUuids == null) {
                entityUuids = new HashSet<>();
                entityUuidByAttribyuteName.put(attrName, entityUuids);
            }
            entityUuids.add(entity.getId());
        }
        return entitiesByUuid.put(entity.getId(), entity);

    }
}
