package com.zygon.rl.world;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.util.NoiseUtil;
import com.zygon.rl.world.character.CharacterSheet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This is an ugly sweater on the ECS, could really use some more structure.
 *
 * @author zygon
 */
public class World {

    private static final System.Logger logger = System.getLogger(World.class.getCanonicalName());

    private final Calendar calendar;
    // These were originally plain static objects, but now they take on some
    // runtime characteristics. E.g. fields have a half-life
    private final GenericEntityManager<Identifable> staticObjects;
    private final ElementEntityManager<CharacterSheet> actors;
    // got lazy, partially immutable
    private Location playerLocation;

    public World(Calendar calendar, GenericEntityManager<Identifable> staticObjects,
            ElementEntityManager<CharacterSheet> actors, Location playerLocation) {
        this.calendar = calendar;
        this.staticObjects = staticObjects;
        this.actors = actors;
        this.playerLocation = playerLocation;
    }

    public World(Calendar calendar) {
        this(calendar, new GenericEntityManager<>(), new ElementEntityManager<>(), null);
    }

    // this is fresh world only
    public World() {
        this(new Calendar(20).addTime(TimeUnit.DAYS.toSeconds(1000)));
    }

    public void add(Identifable id, Location location) {
        staticObjects.add(id, location);
    }

    public void add(CharacterSheet character, Location location) {
        actors.save(character, location);

        if (character.getId().equals("player")) {
            playerLocation = location;
        }
    }

    public boolean canMove(Location destination) {

        // quick check for player location
        if (destination.equals(playerLocation)) {
            return false;
        }

        Terrain terrain = getTerrain(destination);
        if (terrain.getFlag(CommonAttributes.IMPASSABLE.name()) != null) {
            return false;
        }

        // Assumption is you cannot pass an "actor" of any kind
        List<CharacterSheet> get = actors.getByType(destination, null);
        return get == null || get.isEmpty();
    }

    public Map<Location, CharacterSheet> getAll(Location center, String type,
            int radius, boolean includeCenter) {
        Map<Location, List<CharacterSheet>> allByType = actors.getAllByType(center, type, radius, includeCenter);

        // Should only be 1 character in a single space
        return allByType.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().get(0)))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public Map<Location, CharacterSheet> getAll(Location center, String type, int radius) {
        return getAll(center, type, radius, false);
    }

    public List<CharacterSheet> getAll(Location location) {
        return actors.get(location);
    }

    public List<Element> getAllElements(Location location) {
        List<Element> elements = new ArrayList<>();

        List<Element> staticItems = staticObjects.get(location).stream()
                .map(Identifable::getId)
                .map(id -> {
                    // I think having to use this syntax is a language
                    // difficiency wrt generics.
                    Element el = Data.get(id);
                    return el;
                })
                .collect(Collectors.toList());

        elements.addAll(staticItems);

        elements.addAll(actors.get(location));
        return elements;
    }

    public CharacterSheet get(Location location, String type) {
        CharacterSheet character = get(location);

        if (character == null) {
            return null;
        }

        return type == null || character.getType().equals(type) ? character : null;
    }

    public CharacterSheet get(Location location) {
        return actors.getElement(location);
    }

    public List<Identifable> getAll(Location location, String type) {
        // Need to fetch the data out of the templates to check the type
        List<Identifable> identifiables = staticObjects.get(location);

        return identifiables != null
                ? identifiables.stream()
                        .filter(id -> type == null || Data.get(id.getId()).getType().equals(type))
                        .collect(Collectors.toList())
                : List.of();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public List<Location> getPassableNeighbors(Location center) {
        return center.getNeighbors().stream()
                .filter(this::canMove)
                .collect(Collectors.toList());
    }

    public Location getPlayerLocation() {
        return playerLocation;
    }

    public CharacterSheet getPlayer() {
        return get(getPlayerLocation());
    }

    // Only used in a single thread
    private static final byte[] NOISE_BYTES = new byte[8];
    private static final NoiseUtil terrainNoise = new NoiseUtil(new Random().nextInt(), 1.0, 1.0);
    private static final NoiseUtil npcNoise = new NoiseUtil(new Random().nextInt(), 1.0, 1.0);

    public Terrain getTerrain(Location location) {
        double terrainVal = terrainNoise.getScaledValue(location.getX(), location.getY());

        ByteBuffer.wrap(NOISE_BYTES).putDouble(terrainVal);
        int noiseFactor = ByteBuffer.wrap(NOISE_BYTES).getInt(4);
        int noise = Math.abs(noiseFactor % 9);

        if (terrainVal < .4) {
            return Terrain.Ids.PUDDLE.get();
        } else if (terrainVal < .5) {
            if (noise > 3) {
                return Terrain.Ids.DIRT.get();
            } else {
                return Terrain.Ids.GRASS.get();
            }
        } else if (terrainVal < .6) {
            if (noise > 4) {
                return Terrain.Ids.TALL_GRASS.get();
            } else if (noise > 3) {
                return Terrain.Ids.TREE.get();
            } else {
                return Terrain.Ids.GRASS.get();
            }
        } else if (terrainVal < .7) {
            if (noise > 3) {
                return Terrain.Ids.GRASS.get();
            } else {
                return Terrain.Ids.TALL_GRASS.get();
            }
        } else if (terrainVal < .8) {
            if (noise > 6) {
                return Terrain.Ids.TREE.get();
            } else {
                return Terrain.Ids.DIRT.get();
            }
        } else {
            return Terrain.Ids.WALL.get();
        }
    }

    public void move(CharacterSheet element, Location from, Location to) {

        if (!canMove(to)) {
            // TODO: remove eventually once this never happens again
            throw new IllegalStateException();
        } else {
            logger.log(System.Logger.Level.TRACE, "Moving " + element.getId() + ", "
                    + element.getName() + " from " + from + " to " + to);

            remove(element, from);
            add(element, to);
        }
    }

    public void remove(Identifable id, Location from) {
        staticObjects.delete(id, from);
    }

    public void remove(CharacterSheet character, Location from) {
        actors.delete(character, from);
        if (character.getId().equals("player")) {
            playerLocation = null;
        }
    }

    public World setCalendar(Calendar calendar) {
        return new World(calendar, staticObjects, actors, playerLocation);
    }

    public World addTime(long addSeconds) {
        return new World(calendar.addTime(addSeconds), staticObjects, actors, playerLocation);
    }
}
