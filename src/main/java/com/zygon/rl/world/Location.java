package com.zygon.rl.world;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        return getDisplay(x, y, z);
    }

    private static String getDisplay(int x, int y, int z) {
        return "[" + x + "," + y + "," + z + "]";
    }
}
