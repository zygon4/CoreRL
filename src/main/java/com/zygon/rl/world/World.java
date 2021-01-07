package com.zygon.rl.world;

import com.zygon.rl.data.Element;
import com.zygon.rl.util.NoiseUtil;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This is an ugly sweater on the ECS, could really use some more structure.
 *
 * @author zygon
 */
public class World {

    private final Calendar calendar;
    private final EntityManager entityManager;
    // got lazy, partially immutable
    private Location playerLocation;

    public World(Calendar calendar, EntityManager entityManager, Location playerLocation) {
        this.calendar = calendar;
        this.entityManager = entityManager;
        this.playerLocation = playerLocation;
    }

    public World(Calendar calendar) {
        this(calendar, new EntityManager(), null);
    }

    // this is fresh world only
    public World() {
        this(new Calendar(20).addTime(TimeUnit.DAYS.toSeconds(1000)));
    }

    public void add(Element element, Location location) {
        entityManager.save(element, location);
        if (element.getId().equals("player")) {
            playerLocation = location;
        }
    }

    public boolean canMove(Location destination) {
        Entity terrain = getTerrain(destination);
        if (terrain.hasAttribute(CommonAttributes.IMPASSABLE.name())) {
            return false;
        }
        Element dest = getNEW(destination);
        // TODO: check for other non-passable elements
        return dest == null || !dest.getType().equals(CommonAttributes.NPC.name());
    }

    public Map<Location, List<Element>> getAllNEW(Location location, String type, int radius) {
        return entityManager.get(location, type, radius);
    }

    public List<Element> getAllNEW(Location location, String type) {
        return entityManager.get(location, type);
    }

    public List<Element> getAllNEW(Location location) {
        return entityManager.get(location);
    }

    public <T extends Element> T getNEW(Location location, String type) {
        List<Element> entities = getAllNEW(location, type);
        return entities.isEmpty() ? null : (T) entities.iterator().next();
    }

    public Element getNEW(Location location) {
        return getNEW(location, null);
    }

    public <T extends Element> T get(String id) {
        return (T) entityManager.get(id);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    // convience method
    public Location getPlayerLocation() {
        return playerLocation;
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

    public void move(Element element, Location from, Location to) {
        entityManager.delete(element.getId(), from);
        add(element, to);
    }

    public void remove(Element element, Location from) {
        entityManager.delete(element.getId(), from);
        if (element.getId().equals("player")) {
            playerLocation = null;
        }
    }

    public World setCalendar(Calendar calendar) {
        return new World(calendar, entityManager, playerLocation);
    }
}
