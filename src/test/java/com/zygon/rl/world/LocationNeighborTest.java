package com.zygon.rl.world;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 *
 * @author zygon
 */
public class LocationNeighborTest {

    @Test
    public void LocationNeighborTest1() {

        Location start = Location.create(0, 0);
        Set<Location> neighbors = start.getNeighbors(1);

        Assert.assertNotNull(neighbors);

        Assert.assertEquals(8, neighbors.size());
    }

    @Test
    public void LocationNeighborTest2() {

        Location start = Location.create(7, 0);
        Set<Location> neighbors = start.getNeighbors(10);

//        int count = 20;
//        for (int y = -count; y < count; y++) {
//            for (int x = -count; x < count; x++) {
//                if (neighbors.contains(Location.create(x, y))) {
//                    System.out.print("X");
//                } else {
//                    System.out.print(".");
//                }
//            }
//            System.out.println();
//        }
        Assert.assertNotNull(neighbors);

        Assert.assertTrue(neighbors.contains(Location.create(3, 1)));
        Assert.assertTrue(neighbors.contains(Location.create(-1, 1)));
    }

//                        int count = 10;
//                        for (int x = -count; x < count; x++) {
//                            System.out.print(x);
//                        }
//                        System.out.println();
//                        for (int y = count; y > -count; y--) {
//                            for (int x = -count; x < count; x++) {
//                                Location loc = Location.create(x, y);
//
//                                String charAt = ".";
//                                if (world.get(loc) != null) {
//                                    charAt = currentLoc.equals(loc) ? "c" : "p";
//                                }
//                                if (pathToPlayer.contains(loc)) {
//                                    charAt = "x";
//                                }
//                                if (loc.equals(playerLocation)) {
//                                    charAt = "@";
//                                }
//
//                                System.out.print(charAt);
//                            }
//                            System.out.println(" " + y);
//                        }
}
