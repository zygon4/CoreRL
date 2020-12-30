package com.zygon.rl.world;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author zygon
 */
public class CalendarTest {

    @Test
    public void CalendarDiffSecondTest1() {

        Calendar old = new Calendar(0, 0, 1);

        Calendar current = new Calendar(1, 0, 1);

        Assert.assertEquals(1, current.getDifferenceSeconds(old));
    }

    @Test
    public void CalendarDiffSecondTest2() {

        Calendar old = new Calendar(0, 0, 1);

        Calendar current = new Calendar(0, 1, 1);

        Assert.assertEquals(TimeUnit.DAYS.toSeconds(1), current.getDifferenceSeconds(old));
    }

    @Test
    public void CalendarDiffSecondTest3() {

        Calendar old = new Calendar(TimeUnit.DAYS.toSeconds(1) - 1, 0, 1);

        Calendar current = new Calendar(0, 1, 1);

        Assert.assertEquals(1, current.getDifferenceSeconds(old));
    }

    @Test
    public void CalendarDiffSecondTest4() {

        Calendar old = new Calendar(0, 0, 1);

        Calendar current = new Calendar(0, 0, 1);
        current = current.addTime(TimeUnit.DAYS.toSeconds(1));

        Assert.assertEquals(TimeUnit.DAYS.toSeconds(1), current.getDifferenceSeconds(old));
    }

    @Test
    public void CalendarDiffSecondTest5() {

        Calendar old = new Calendar(5, 1, 2);

        Calendar current = new Calendar(0, 2, 2);

        Assert.assertEquals(TimeUnit.DAYS.toSeconds(1) - 5, current.getDifferenceSeconds(old));
    }
}
