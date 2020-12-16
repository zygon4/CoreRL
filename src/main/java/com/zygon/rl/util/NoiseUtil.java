package com.zygon.rl.util;

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

    // Returns a scaled noise value between 0.0 inclusive and 1.0 exclusive.
    public double getScaledValue(int x, int y) {
        double noiseVal = getValue(x, y);

        // This is for Simplex noise
        noiseVal += 0.5;

        return noiseVal;
    }

    // Returns a noise value offset with the frequency/octaves values.
    public double getValue(int x, int y) {

        double xx = frequency * (((double) x) / 800l);
        double yy = frequency * (((double) y) / 800l);

        double terrainVal = octaves * noise.eval(xx, yy);

        // TBD smoothing if desired
//        terrainVal = Math.pow(terrainVal, 2.00);
        return terrainVal;
    }
}
