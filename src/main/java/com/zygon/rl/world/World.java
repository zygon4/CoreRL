package com.zygon.rl.world;

import com.zygon.rl.util.NoiseUtil;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
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

    public boolean canMove(Location destination) {
        Entity terrain = getTerrain(destination);
        if (terrain.hasAttribute(CommonAttributes.IMPASSABLE.name())) {
            return false;
        }
        Entity dest = get(destination);
        return dest == null || dest.getAttribute(CommonAttributes.IMPASSABLE.name()) == null;
    }

    // These APIS (the "gets") are becoming weird..
    public Set<Entity> getAll(Location location, Location origin) {
        Set<Entity> entities = entityManager.findAll(
                EntityManager.Query.builder()
                        .setLocation(location)
                        .setOrigin(origin)
                        .build());
        return entities == null ? Collections.emptySet() : Collections.unmodifiableSet(entities);
    }

    // These APIS (the "gets") are becoming weird..
    public Set<Entity> getAll(Location location, int radius, Map<String, String> attrs) {

        EntityManager.Query.Builder queryBuilder = EntityManager.Query.builder();

        for (var attr : attrs.entrySet()) {
            queryBuilder.addAttribute(attr.getKey(), attr.getValue());
        }

        // TODO: location/radius should be part of the query, not after
        Set<Entity> entities = entityManager.findAll(queryBuilder.build()).stream()
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

    // Only used in a single thread
    private static final byte[] NOISE_BYTES = new byte[8];
    private static final NoiseUtil terrainNoise = new NoiseUtil(new Random().nextInt(), 1.0, 1.0);
    private static final NoiseUtil npcNoise = new NoiseUtil(new Random().nextInt(), 1.0, 1.0);

    // TODO: this should be completely customizable via json/config
    // This is also weird because terrain tiles are NOT being set in the world ECS
    // this is just a convenient way to get tile information.
    public Entity getTerrain(Location location) {
        double terrainVal = terrainNoise.getScaledValue(location.getX(), location.getY());

        ByteBuffer.wrap(NOISE_BYTES).putDouble(terrainVal);
        int noiseFactor = ByteBuffer.wrap(NOISE_BYTES).getInt(4);
        int noise = Math.abs(noiseFactor % 9);

        if (terrainVal < .4) {
            return Entities.PUDDLE;
        } else if (terrainVal < .5) {
            if (noise > 3) {
                return Entities.DIRT;
            } else {
                return Entities.GRASS;
            }
        } else if (terrainVal < .6) {
            if (noise > 4) {
                return Entities.TALL_GRASS;
            } else if (noise > 2) {
                return Entities.TREE;
            } else {
                return Entities.GRASS;
            }
        } else if (terrainVal < .7) {
            if (noise > 3) {
                return Entities.GRASS;
            } else {
                return Entities.TALL_GRASS;
            }
        } else if (terrainVal < .8) {
            if (noise > 6) {
                return Entities.TREE;
            } else {
                return Entities.DIRT;
            }
        } else {
            return Entities.WALL;
        }
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
