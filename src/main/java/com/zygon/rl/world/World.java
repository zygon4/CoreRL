package com.zygon.rl.world;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author zygon
 */
public class World {

    private final EntityManager entityManager = new EntityManager();

    public World move(Entity entity, Location destination) {
        entityManager.delete(entity.getId());
        entityManager.save(entity.copy().setLocation(destination).build());
        return this;
    }

    public World add(Entity entity) {
        entityManager.save(entity);
        return this;
    }

    public Set<Entity> getAll(Location location) {
        Set<Entity> entities = entityManager.findAll(
                EntityManager.Query.builder()
                        .setLocation(location).build());
        return entities == null ? Collections.emptySet() : Collections.unmodifiableSet(entities);
    }

    public Entity get(Location location) {
        Set<Entity> entities = getAll(location);
        return entities == null ? null : entities.iterator().next();
    }

    public Entity get(UUID uuid) {
        return entityManager.get(uuid);
    }
}
