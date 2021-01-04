package com.zygon.rl.game.ui;

import com.stewsters.util.shadow.twoDimention.LitMap2d;

final class LitMap2DImpl implements LitMap2d {

    private final float[][] lightResistances;
    private final float[][] light;

    public LitMap2DImpl(float[][] lightResistances) {
        this.lightResistances = lightResistances;
        this.light = new float[this.lightResistances.length][this.lightResistances[0].length];
    }

    @Override
    public void setLight(int startx, int starty, float force) {
        // Inversed Y
        this.light[startx][this.light[0].length - starty - 1] = force;
    }

    @Override
    public int getXSize() {
        return lightResistances.length;
    }

    @Override
    public int getYSize() {
        return lightResistances[0].length;
    }

    @Override
    public float getLight(int currentX, int currentY) {
        return this.light[currentX][currentY];
    }

    @Override
    public float getResistance(int currentX, int currentY) {
        return lightResistances[currentX][currentY];
    }

    @Override
    public void addLight(int currentX, int currentY, float bright) {
        // Inversed Y because the zircon screen's columns are bottom left, not top left
        this.light[currentX][this.light[0].length - currentY] = bright;
    }

}
