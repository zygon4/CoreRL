package com.zygon.rl.world;

/**
 *
 * @author zygon
 */
public interface WorldSpawn {

    default int getLivingSpawnFrequency() {
        return 50;
    }

    default int getCityBuildingDistance() {
        return 20;
    }
}
