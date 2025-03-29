package com.zygon.rl.world.character;

import com.zygon.rl.data.PoolData;
import com.zygon.rl.world.Attribute;

/**
 * A runtime pool for any character status. E.g. health, stamina, etc.
 *
 * @author zygon
 */
public final class Pool {

    private final PoolData poolData;
    private final int points;

    public Pool(PoolData poolData, int points) {
        this.poolData = poolData;
        this.points = points;
    }

    /**
     * Sets the pool current value to {@link PoolData#getMax() }
     *
     * @param poolData
     * @return {@link Pool}
     */
    public static Pool createMax(PoolData poolData) {
        return new Pool(poolData, poolData.getMax());
    }

    /**
     * Sets the pool current value to the midway point between min and max.
     *
     * @param poolData
     * @return {@link Pool}
     */
    public static Pool createMid(PoolData poolData) {
        return new Pool(poolData, (poolData.getMax() - poolData.getMin()) / 2);
    }

    public PoolData getPoolData() {
        return poolData;
    }

    public String getDescription() {
        return poolData.getDescription();
    }

    public int getMax() {
        return poolData.getMax();
    }

    public int getMin() {
        return poolData.getMin();
    }

    public String getName() {
        return poolData.getName();
    }

    public int getPoints() {
        return points;
    }

    public Pool decrement(int points) {
        int val = Math.max(getPoints() - points, getMin());
        return new Pool(poolData, val);
    }

    public Pool increment(int points) {
        int val = Math.min(getPoints() + points, getMax());
        return new Pool(poolData, val);
    }

    public Pool set(int points) {
        int val = Math.min(points, getMax());
        val = Math.max(points, getMin());
        return new Pool(poolData, val);
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
