package com.zygon.rl.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Mutable state here..
 *
 * @author zygon
 */
public class EntityManager {

    public static class Query {

        private final Map<String, String> attributes;
        private final Location origin;
        private final Location location;

        private Query(Builder builder) {
            this.attributes = Collections.unmodifiableMap(builder.attributes);
            this.origin = builder.origin;
            this.location = builder.location;
        }

        public Map<String, String> getAttributes() {
            return attributes;
        }

        public Location getLocation() {
            return location;
        }

        public Location getOrigin() {
            return origin;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private Map<String, String> attributes = new HashMap<>();
            private Location origin;
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

            /**
             * Returns entities at the given origin location
             *
             * @param origin
             * @return
             */
            public Builder setOrigin(Location origin) {
                this.origin = origin;
                return this;
            }

            public Query build() {
                return new Query(this);
            }
        }
    }

    // TBD: caching if needed
    private final Map<UUID, Entity> entitiesByUuid = new HashMap<>();

    private static final Predicate<Entity> IS_NOT_DELETED = (ent) -> {
        return ent.getAttribute(CommonAttributes.DELETED.name()) == null;
    };

    // Basic CRUD for entities..
    public void delete(UUID uuid, boolean erase) {
        Entity existing = get(uuid);

        if (erase) {
            // remove from uuid map
            entitiesByUuid.remove(uuid);
        } else {
            // don't delete, just flag
            save(existing.copy()
                    .setAttributeValue(CommonAttributes.DELETED.name(), Boolean.TRUE.toString())
                    .build());
        }
    }

    public void delete(UUID uuid) {
        delete(uuid, false);
    }

    public Set<Entity> findAll(Query query) {

        Set<Entity> entities = new HashSet<>();

        for (var ent : entitiesByUuid.entrySet()) {
            if (IS_NOT_DELETED.test(ent.getValue())) {

                // sad double loop, for each entity, check each attribute to
                // produce an intercection of entities with the query attrs
                boolean include = true;
                for (var attribute : query.getAttributes().entrySet()) {
                    String attrName = attribute.getKey();
                    String attrValue = attribute.getValue();

                    if (!ent.getValue().hasAttribute(attrName)
                            || attrValue != null && !ent.getValue().getAttributeValue(attrName).equals(attrValue)) {
                        include = false;
                        break;
                    }
                }

                if (include) {
                    Location queryLocation = query.getLocation();
                    if (queryLocation != null && !ent.getValue().getLocation().equals(queryLocation)) {
                        include = false;
                    }
                }

                if (include) {
                    Location queryOrigin = query.getOrigin();
                    if (queryOrigin != null && !ent.getValue().getOrigin().equals(queryOrigin)) {
                        include = false;
                    }
                }

                if (include) {
                    entities.add(ent.getValue());
                }
            }
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
        return entitiesByUuid.put(entity.getId(), entity);
    }
}
