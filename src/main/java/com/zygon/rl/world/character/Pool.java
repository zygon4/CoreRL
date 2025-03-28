package com.zygon.rl.world.character;

import com.zygon.rl.world.Attribute;

/**
 * A pool for any character status. E.g. health, stamina, etc.
 *
 * @author zygon
 */
public final class Pool {

    private final String name;
    private final String description;
    private final int points;
    private final int min;
    private final int max;

    private Pool(String name, String description, int points, int min, int max) {
        this.name = name;
        this.description = description;
        this.points = points;
        this.min = min;
        this.max = max;
    }

    public static Pool create(String name, String description, int points,
            int min, int max) {
        return new Pool(name, description, points, min, max);
    }

    public String getDescription() {
        return description;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public Pool decrement(int points) {
        int val = Math.max(this.points - points, min);
        return new Pool(name, description, val, min, max);
    }

    public Pool increment(int points) {
        int val = Math.min(this.points + points, max);
        return new Pool(name, description, val, min, max);
    }

    public Pool set(int points) {
        int val = Math.min(points, max);
        val = Math.max(points, min);
        return new Pool(name, description, val, min, max);
    }

    public Attribute getAttribute() {
        return Attribute.builder()
                .setName(getName())
                .setDescription(getDescription())
                .setValue(String.valueOf(getPoints()))
                .build();
    }

    @Override
    public String toString() {
        return getAttribute().toString();
    }
}
