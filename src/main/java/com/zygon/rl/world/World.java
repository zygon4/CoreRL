package com.zygon.rl.world;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This is an ugly sweater on the ECS, could really use some more structure.
 *
 * @author zygon
 */
public class World {

    private final Calendar calendar;
    private final EntityManager entityManager;

    public World(Calendar calendar, EntityManager entityManager) {
        this.calendar = calendar;
        this.entityManager = entityManager;
    }

    public World(Calendar calendar) {
        this(calendar, new EntityManager());
    }

    // this is fresh world only
    public World() {
        this(new Calendar(20).addTime(TimeUnit.DAYS.toSeconds(1000)));
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

    public Set<Entity> getAll(Location location, int radius) {
        // This is a specific query for living beings.. could be more generic
        Set<Entity> entities = entityManager.findAll(
                EntityManager.Query.builder()
                        .addAttribute(CommonAttributes.LIVING.name())
                        .build()).stream()
                .filter(ent -> ent.getLocation().getDistance(location) <= radius)
                .collect(Collectors.toSet());

        return entities == null
                ? Collections.emptySet() : Collections.unmodifiableSet(entities);
    }

    public Set<Entity> getAll(Location location) {
        return getAll(location, null);
    }

    public Entity get(Location location) {
        Set<Entity> entities = getAll(location, null);
        return entities.isEmpty() ? null : entities.iterator().next();
    }

    public Entity get(UUID uuid) {
        return entityManager.get(uuid);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void move(Entity entity, Location destination) {
        entityManager.delete(entity.getId());
        entityManager.save(entity.copy().setLocation(destination).build());
    }

    public void remove(Entity entity) {
        entityManager.delete(entity.getId());
    }

    public World setCalendar(Calendar calendar) {
        return new World(calendar, entityManager);
    }
}
