package com.zygon.rl.world;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 *
 * @author zygon
 */
public class LocationTest {

    @Test
    public void LocationPathTest1() {

        Location start = Location.create(-1, 0);
        Location dest = Location.create(1, 0);

        List<Location> path = start.getPath(dest);
        Assert.assertNotNull(path);

        Assert.assertEquals(2, path.size());
        Assert.assertEquals(Location.create(0, 0), path.get(0));
        Assert.assertEquals(Location.create(1, 0), path.get(1));
    }

    @Test
    public void LocationPathTest2() {

        Location start = Location.create(1, 0);
        Location dest = Location.create(-1, 0);

        List<Location> path = start.getPath(dest);
        Assert.assertNotNull(path);

        Assert.assertEquals(2, path.size());
        Assert.assertEquals(Location.create(0, 0), path.get(0));
        Assert.assertEquals(Location.create(-1, 0), path.get(1));
    }

    @Test
    public void LocationPathTest3() {

        Location start = Location.create(-1, -1);
        Location dest = Location.create(1, 1);

        List<Location> path = start.getPath(dest, (l) -> {
            if (l.getX() == 0 && l.getY() == 0) {
                return false;
            } else {
                return true;
            }
        });
        Assert.assertNotNull(path);

        Assert.assertEquals(3, path.size());
        Assert.assertEquals(Location.create(-1, 0), path.get(0));
        Assert.assertEquals(Location.create(0, 1), path.get(1));
        Assert.assertEquals(Location.create(1, 1), path.get(2));
    }

    @Test
    public void LocationPathTest4() {

        Location start = Location.create(1, 1);
        Location dest = Location.create(-1, -1);

        List<Location> path = start.getPath(dest, (l) -> {
            if (l.getX() == 0 && l.getY() == 0) {
                return false;
            } else {
                return true;
            }
        });
        Assert.assertNotNull(path);

        Assert.assertEquals(3, path.size());
        Assert.assertEquals(Location.create(0, 1), path.get(0));
        Assert.assertEquals(Location.create(-1, 0), path.get(1));
        Assert.assertEquals(Location.create(-1, -1), path.get(2));
    }

    @Test
    public void LocationPathTest5() {

        Location start = Location.create(1, 0);
        Location dest = Location.create(3, 0);

        List<Location> path = start.getPath(dest, (l) -> {
            if (l.getX() == 2 && l.getY() == 0) {
                return false;
            } else {
                return true;
            }
        });
        Assert.assertNotNull(path);

        Assert.assertEquals(2, path.size());
        Assert.assertTrue(path.get(0).equals(Location.create(2, 1))
                || path.get(0).equals(Location.create(2, -1)));
        Assert.assertEquals(Location.create(3, 0), path.get(1));
    }

    @Test
    public void LocationPathTest6() {

        Location start = Location.create(3, 1);
        Location dest = Location.create(1, 1);

        List<Location> path = start.getPath(dest, (l) -> {
            return true;
        });
        Assert.assertNotNull(path);

        Assert.assertEquals(2, path.size());
        Assert.assertEquals(Location.create(2, 1), path.get(0));
        Assert.assertEquals(Location.create(1, 1), path.get(1));
    }
}
