package com.zygon.rl.world;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.Identifable;
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

    public void add(String id, Location location) {
        staticObjects.add(() -> id, location);
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

        Entity terrain = getTerrain(destination);
        if (terrain.hasAttribute(CommonAttributes.IMPASSABLE.name())) {
            return false;
        }

        // Assumption is you cannot pass an "actor" of any kind
        List<CharacterSheet> get = actors.getByType(destination, null);
        return get == null || get.isEmpty();
    }

    public Map<Location, CharacterSheet> getAll(Location center, String type, int radius) {
        Map<Location, List<CharacterSheet>> allByType = actors.getAllByType(center, type, radius);

        // Should only be 1 character in a single space
        return allByType.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().get(0)))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public List<CharacterSheet> getAll(Location location) {
        return actors.get(location);
    }

    public List<Element> getAllElements(Location location) {
        List<Element> elements = new ArrayList<>();

        List<Element> staticItems = staticObjects.get(location).stream()
                .map(Identifable::getId)
                .map(id -> {
                    //I think having to use this syntax is a language difficiency
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

    public List<String> getAll(Location location, String type) {
        // Need to fetch the data out of the templates to check the type
        List<Identifable> identifiables = staticObjects.get(location);

        return identifiables != null
                ? identifiables.stream()
                        .filter(id -> type == null || Data.get(id.getId()).getType().equals(type))
                        .map(Identifable::getId)
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

    public void remove(CharacterSheet character, Location from) {
        actors.delete(character, from);
        if (character.getId().equals("player")) {
            playerLocation = null;
        }
    }

    public World setCalendar(Calendar calendar) {
        return new World(calendar, staticObjects, actors, playerLocation);
    }
}
