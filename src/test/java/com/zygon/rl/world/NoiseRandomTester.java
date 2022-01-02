package com.zygon.rl.world;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 */
public class NoiseRandomTester {

    @Test
    public void test() {
        Map<Location, Integer> randoms = new HashMap<>();

        for (int i = 0; i < 1000; i++) {
            for (int y = 0; y < 1000; y++) {
                Location l = Location.create(i, y);
                Random noiseRandom = World.getNoiseRandom(l);
                randoms.put(l, noiseRandom.nextInt());
            }
        }

        for (int i = 0; i < 1000; i++) {
            for (int y = 0; y < 1000; y++) {
                Location l = Location.create(i, y);
                Random noiseRandom = World.getNoiseRandom(l);

                int sameRandom = noiseRandom.nextInt();

                Assert.assertEquals(randoms.get(l).intValue(), sameRandom);

            }
        }
    }
}
