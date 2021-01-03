package com.zygon.rl.world;

import com.stewsters.util.math.Point2i;
import com.stewsters.util.pathing.twoDimention.heuristic.ManhattanHeuristic2d;
import com.stewsters.util.pathing.twoDimention.pathfinder.AStarPathFinder2d;
import com.stewsters.util.pathing.twoDimention.shared.BoundingBox2d;
import org.apache.commons.math3.util.Pair;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Location
 *
 */
public class Location {

    // singleton cache
    private static final Map<String, Location> KNOWN_LOCATIONS = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Location> eldest) {
            return size() > 1024;
        }
    };

    // For use with path finding
    private static final int BOX_SIZE = 50;
    private static final BoundingBox2d MAP = new BoundingBox2d() {
        @Override
        public int getXSize() {
            return BOX_SIZE;
        }

        @Override
        public int getYSize() {
            return BOX_SIZE;
        }
    };
    // TO USE THIS IT MEANS WE NEED TO NORMALIZE ALL VALUES TO 0-49
    private static final AStarPathFinder2d PATH_FINDER = new AStarPathFinder2d(MAP, 100);

    private final int x;
    private final int y;
    private final int z;
    private final String hash;

    private Location(int x, int y, int z, String hash) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.hash = hash;
    }

    public double getDistance(Location o) {

        double total = 0.0;

        total += Math.pow(this.x - o.x, 2);
        total += Math.pow(this.y - o.y, 2);
        total += Math.pow(this.z - o.z, 2);

        return Math.sqrt(total);
    }

    // This either goes here or in a util that you pass the locations (plus game info) into
    public List<Location> getPath(final Location to, Function<Location, Boolean> canTraverse) {

        Pair<Location, Location> projected = projectToBox(this, to, BOX_SIZE);

        final int toDeltaX = to.getX() - projected.getSecond().getX();
        final int toDeltaY = to.getY() - projected.getSecond().getY();

        Optional<List<Point2i>> foundPath = PATH_FINDER.findPath(
                // canTraverse2d
                (sx, sy, tx, ty) -> {
                    Location realLoc = Location.create(tx + toDeltaX, ty + toDeltaY);

                    // Ignoring if the dest is the current location
                    // or if the "can traverse" is the ultimate destination
                    // For some reason the algo flakes if that's the case.
                    // This seems to be good enough.
                    if ((sx == tx && sy == ty) || (realLoc.getX() == to.getX() && realLoc.getY() == to.getY())) {
                        return true;
                    }

                    return canTraverse.apply(realLoc);
                },
                // canOccupy2d - this is used as a quick check if the destination
                // is occupied. If true, then it will return an empty path. This
                // isn't useful for the game because most destinations (player, npc, etc)
                // will be "unoccupiable". So just always return true.
                (tx, ty) -> true,
                (int sx, int sy, int tx, int ty) -> 1.0f, // TODO: get terrain cost
                new ManhattanHeuristic2d(),
                true, // allowDiagMovement
                projected.getFirst().getX(), projected.getFirst().getY(),
                projected.getSecond().getX(), projected.getSecond().getY()
        );

        return foundPath.isPresent() ? foundPath.get().stream()
                .skip(1) // skip first element (current location)
                .map(point2d -> Location.create(
                point2d.x + toDeltaX, point2d.y + toDeltaY))
                .collect(Collectors.toList()) : null;
    }

    public List<Location> getPath(Location to) {
        return getPath(to, (t) -> true);
    }

    public Set<Location> getNeighbors() {
        Set<Location> neighors = new HashSet<>();

        neighors.add(Location.create(x + 1, y, z));
        neighors.add(Location.create(x + 1, y + 1, z));

        neighors.add(Location.create(x - 1, y, z));
        neighors.add(Location.create(x - 1, y - 1, z));

        neighors.add(Location.create(x, y + 1, z));
        neighors.add(Location.create(x - 1, y + 1, z));

        neighors.add(Location.create(x, y - 1, z));
        neighors.add(Location.create(x + 1, y - 1, z));

        return neighors;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public static Location create(int x, int y, int z) {
        String hash = getDisplay(x, y, z);
        if (KNOWN_LOCATIONS.containsKey(hash)) {
            Location loc = KNOWN_LOCATIONS.get(hash);

            if (loc.getX() != x || loc.getY() != y) {
                // hash collision
                throw new IllegalStateException(loc.toString());
            }

            return loc;
        } else {
            Location location = new Location(x, y, z, hash);
            KNOWN_LOCATIONS.put(hash, location);
            return location;
        }
    }

    public static Location create(int x, int y) {
        return create(x, y, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Location that = (Location) o;

        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.hash);
        return hash;
    }

    @Override
    public String toString() {
        return hash;
    }

    private static String getDisplay(int x, int y, int z) {
        return "[" + x + "," + y + "," + z + "]";
    }

    // To project against the box size and become a value between 0-49
    // e.g.
    private static Pair<Location, Location> projectToBox(Location from, Location to, int boxSize) {
        // first place the center. e.g. -5 -> 25, 218 -> 25
        int diffX = Math.abs(from.getX() - to.getX());
        int diffY = Math.abs(from.getY() - to.getY());

        int xFiller = (boxSize - diffX) / 2; // off by 1s? lollolol
        int yFiller = (boxSize - diffY) / 2;

        // We loose ordinality, need to double check against the originals
        Location maybeFrom = Location.create(xFiller, yFiller);
        Location maybeTo = Location.create(xFiller + diffX, yFiller + diffY);

        // what about Y?
        if (from.getX() > to.getX()) {
            return Pair.create(maybeTo, maybeFrom);
        } else {
            return Pair.create(maybeFrom, maybeTo);
        }
    }
}
