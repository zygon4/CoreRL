package com.zygon.rl.util;

import java.util.List;
import java.util.Random;

import com.stewsters.util.noise.OpenSimplexNoise;

/**
 * Noise util that uses Simplex noise.
 */
public final class NoiseUtil {

    private final OpenSimplexNoise noise;
    private final double frequency;
    private final double octaves;

    public NoiseUtil(int seed, Double frequency, Double octaves) {
        this.noise = new OpenSimplexNoise(seed);
        this.frequency = frequency != null ? frequency.doubleValue() : 1.0;
        this.octaves = octaves != null ? octaves.doubleValue() : 1.0;
    }

    public NoiseUtil(int seed) {
        this(seed, null, null);
    }

    public double round(double val) {
        double roundOff = Math.round(val * 100.0) / 100.0;
        return roundOff;
    }

    // Returns a scaled noise value between 0.0 inclusive and 1.0 exclusive.
    public double getScaledValue(int x, int y) {
        double noiseVal = getValue(x, y);

        // This is for Simplex noise
        noiseVal += 0.5;

        return noiseVal;
    }

    // Returns a noise value offset with the frequency/octaves values.
    public double getValue(int x, int y) {

        double xx = frequency * (((double) x) / 200l);
        double yy = frequency * (((double) y) / 200l);

        double terrainVal = octaves * noise.eval(xx, yy);

        // TBD smoothing if desired
//        terrainVal = Math.pow(terrainVal, 2.00);
        return terrainVal;
    }

    public static double scale(final double valueIn, final double baseMin,
            final double baseMax, final double limitMin, final double limitMax) {
        return ((limitMax - limitMin) * (valueIn - baseMin) / (baseMax - baseMin)) + limitMin;
    }

    public static int getWeightedRandom(Random random, List<Integer> weights) {

        // Compute the total weight of all items together
        int totalWeight = 0;
        for (Integer i : weights) {
            totalWeight += i.intValue();
        }

        // Now choose a random item
        int randomIndex = -1;
        double rand = random.nextDouble() * totalWeight;
        for (int i = 0; i < weights.size(); ++i) {
            rand -= weights.get(i);
            if (rand <= 0.0d) {
                randomIndex = i;
                break;
            }
        }

        return randomIndex;
    }
}
