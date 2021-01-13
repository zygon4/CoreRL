package com.zygon.rl.world;

/**
 *
 * @author zygon
 */
public interface WorldSpawn {

    default int getFrequency() {
        return 50;
    }
}
