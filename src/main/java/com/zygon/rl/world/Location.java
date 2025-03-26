package com.zygon.rl.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.stewsters.util.math.Point2i;
import com.stewsters.util.pathing.twoDimention.heuristic.ManhattanHeuristic2d;
import com.stewsters.util.pathing.twoDimention.pathfinder.AStarPathFinder2d;
import com.stewsters.util.pathing.twoDimention.shared.BoundingBox2d;

import org.apache.commons.math3.util.Pair;

/**
 * Location
 *
 */
public class Location {

    private static final Cache<String, Location> KNOWN_LOCATIONS = CacheBuilder.newBuilder()
            .maximumSize(1024 * 100)
            .build();

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

    /**
     * Returns a straight line to the target without any notion of traversal or
     * path finding.
     *
     * @param to
     * @return
     */
    public List<Location> getLine(final Location to) {
        return getSuperCoverLine(this, to);
    }

    public List<Location> getPath(final Location to,
            Function<Location, Boolean> canTraverse) {

        Pair<Location, Location> projected = projectToBox(this, to, BOX_SIZE);

        final int toDeltaX = to.getX() - projected.getSecond().getX();
        final int toDeltaY = to.getY() - projected.getSecond().getY();

        // TODO: something is funny here, paths are jumping around in certain cases
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

    /**
     * Returns the immediate neighbors.
     *
     * @param includeThis - include this location in the result
     * @return
     */
    public Set<Location> getNeighbors(boolean includeThis) {
        Set<Location> neighors = new HashSet<>();

        if (includeThis) {
            neighors.add(this);
        }

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

    public Set<Location> getNeighbors() {
        return getNeighbors(false);
    }

    private static final int NUM_POINTS = 15;

    public Set<Location> getNeighbors(int radius, boolean includeThis) {
        // always include all immediate neighbors (special case of radius = 1)
        // TODO: maybe just collapse the methods?
        Set<Location> neighors = new HashSet<>(getNeighbors(includeThis));

        double radiusDouble = 0;
        int numPoints = 0;

        for (int i = 1; i < radius; i++) {

            radiusDouble = i;
            numPoints = NUM_POINTS * i;

            for (int points = 0; points < numPoints; ++points) {
                final double angle = Math.toRadians(((double) points / numPoints) * 360d);

                Location l = Location.create(
                        getX() + (int) (Math.cos(angle) * radiusDouble),
                        getY() + (int) (Math.sin(angle) * radiusDouble));

                if (!neighors.contains(l) && !l.equals(this)) {
                    neighors.add(l);
                }
            }
        }

        return neighors;
    }

    public Set<Location> getNeighbors(int radius) {
        return getNeighbors(radius, false);
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

    public Location round(int freq) {
        return Location.create(
                (int) (freq * (Math.round(getX() / freq))),
                (int) (freq * (Math.round(getY() / freq))));
    }

    public static Location create(int x, int y, int z) {
        String hash = getDisplay(x, y, z);

        Location cachedLoc = KNOWN_LOCATIONS.getIfPresent(hash);
        if (cachedLoc != null) {
            synchronized (KNOWN_LOCATIONS) {
                // double-check
                Location loc = KNOWN_LOCATIONS.getIfPresent(hash);

                // Was de-cached
                if (loc == null) {
                    loc = store(x, y, z, hash);
                }

                if (loc == null) {
                    throw new IllegalStateException("loc is null? " + hash
                            + ". contains?" + KNOWN_LOCATIONS.asMap().containsKey(hash) + "?");
                }

                if (loc.getX() != x || loc.getY() != y) {
                    // hash collision
                    throw new IllegalStateException(loc.toString());
                }

                return loc;
            }
        } else {
            return store(x, y, z, hash);
        }
    }

    private static Location store(int x, int y, int z, String hash) {
        Location location = new Location(x, y, z, hash);
        KNOWN_LOCATIONS.put(hash, location);
        return location;
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

    // This is a line, not a search algorithm, suitable for straight line targetting.
    // From https://www.redblobgames.com/grids/line-drawing.html
    private List<Location> getSuperCoverLine(Location p0, Location p1) {
        double dx = p1.getX() - p0.getX(), dy = p1.getY() - p0.getY();
        double nx = Math.abs(dx), ny = Math.abs(dy);
        int sign_x = dx > 0 ? 1 : -1, sign_y = dy > 0 ? 1 : -1;

        Location p = Location.create(p0.getX(), p0.getY());
        List<Location> points = new ArrayList<>();
        points.add(Location.create(p.getX(), p.getY()));

        for (double ix = 0, iy = 0; ix < nx || iy < ny;) {
            double decision = (1 + 2 * ix) * ny - (1 + 2 * iy) * nx;
            if (decision == 0) {
                // next step is diagonal
                p = Location.create(p.getX() + sign_x, p.getY() + sign_y);
                ix++;
                iy++;
            } else if (decision < 0) {
                // next step is horizontal
                p = Location.create(p.getX() + sign_x, p.getY());
                ix++;
            } else {
                // next step is vertical
                p = Location.create(p.getX(), p.getY() + sign_y);
                iy++;
            }
            points.add(Location.create(p.getX(), p.getY()));
        }
        return points;
    }

    // To project against the box size and become a value between 0-49
    // e.g.
    private static Pair<Location, Location> projectToBox(Location from,
            Location to, int boxSize) {
        // first place the center. e.g. -5 -> 25, 218 -> 25
        int diffX = to.getX() - from.getX();
        int diffY = to.getY() - from.getY();

        int xFiller = (boxSize - diffX) / 2; // off by 1s? lollolol
        int yFiller = (boxSize - diffY) / 2;

        // We loose ordinality, need to double check against the originals
        Location maybeFrom = Location.create(xFiller, yFiller);
        Location maybeTo = Location.create(xFiller + diffX, yFiller + diffY);

        return Pair.create(maybeFrom, maybeTo);
    }
}
