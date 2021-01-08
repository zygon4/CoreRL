package com.zygon.rl.world;

import com.zygon.rl.data.Element;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author zygon
 */
public class EntityManagerTest {

    @Test
    public void test1() {
        EntityManager man = new EntityManager();

        Element e1 = new Element();
        e1.setId("a");
        Element e2 = new Element();
        e2.setId("a");
        Element e3 = new Element();
        e3.setId("b");
        Element e4 = new Element();
        e4.setId("b");

        man.save(e1, Location.create(0, 0));
        man.save(e2, Location.create(0, 0));
        man.save(e3, Location.create(0, 0));
        man.save(e4, Location.create(0, 0));

        man.get(Location.create(0, 0));

        man.delete("b", Location.create(0, 0));

        Assert.assertEquals(3, man.get(Location.create(0, 0)).size());
    }
}
