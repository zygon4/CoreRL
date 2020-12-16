package com.zygon.rl.util;

import org.junit.Test;

import java.util.Random;

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

        for (int i = 1000; i < 80000; i++) {

            double val = util.getValue(i, 8);

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

}
