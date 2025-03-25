package com.zygon.rl.util;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldRegion;

import org.junit.Test;

/**
 *
 * @author zygon
 */
public class NoiseTest {

    @Test
    public void NoiseUtilTest() {

        Random random = new Random();
        NoiseUtil util = new NoiseUtil(random.nextInt(), null, null);

        double min = 0;
        double max = 0;

        for (int i = 0; i < 1000000; i++) {

            double val = util.getValue(i, 8);

            val = NoiseUtil.scale(val, -0.90, 0.90, 0.0, 100.0);
            val = util.round(val);

            if (val > max) {
                max = val;
            }
            if (val < min) {
                min = val;
            }

            System.out.print(val + " => ");
            System.out.println(util.getScaledValue(i, 8));
        }

        System.out.println(min);
        System.out.println(max);
    }

    @Test
    public void NoiseTest() {

        Map<WorldRegion, Integer> dist = new TreeMap<>();
        World world = new World();

        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                WorldRegion region = world.getRegion(Location.create(i, j));
                Integer count = dist.computeIfAbsent(region, d -> 0);
                dist.put(region, count.intValue() + 1);
            }
        }

        for (WorldRegion key : dist.keySet()) {
            System.out.println(key.name() + "," + dist.get(key));
        }
    }

    @Test
    public void NoiseTest2() {

        Map<Double, Integer> dist = new TreeMap<>();
        World world = new World();

//        for (int i = 0; i < 1000; i++) {
//            for (int j = 0; j < 1000; j++) {
//                WorldRegion region = world.getRegion(Location.create(i, j));
//                Integer count = dist.computeIfAbsent(region, d -> 0);
//                dist.put(region, count.intValue() + 1);
//            }
//        }
//
//        for (WorldRegion key : dist.keySet()) {
//            System.out.println(key.name() + "," + dist.get(key));
//        }
    }

}
