package com.zygon.rl.world;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * This is an ugly sweater on the ECS.
 *
 * @author zygon
 */
public class World {

    private final EntityManager entityManager = new EntityManager();

    public void move(Entity entity, Location destination) {
        entityManager.delete(entity.getId());
        entityManager.save(entity.copy().setLocation(destination).build());
    }

    public void add(Entity entity) {
        entityManager.save(entity);
    }

    public Set<Entity> getAll(Location location, Location origin) {
        Set<Entity> entities = entityManager.findAll(
                EntityManager.Query.builder()
                        .setLocation(location)
                        .setOrigin(origin)
                        .build());
        return entities == null ? Collections.emptySet() : Collections.unmodifiableSet(entities);
    }

    public Set<Entity> getAll(Location location) {
        return getAll(location, null);
    }

    public Entity get(Location location) {
        Set<Entity> entities = getAll(location, null);
        return entities == null ? null : entities.iterator().next();
    }

    public Entity get(UUID uuid) {
        return entityManager.get(uuid);
    }
}
