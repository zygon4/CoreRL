package com.zygon.rl.game.ui.render;

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

    // This fixes an issue that seems to come from the LitMap2d library, it
    // shows a shadow on tiles directly south of the player when that tiles is
    // only supposed to cause shadows behind it.
    int adjustedY = this.light[0].length - starty;
    adjustedY = Math.min(adjustedY, this.light[0].length - 1);
    this.light[startx][adjustedY] = force;
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
    this.light[currentX][this.light[0].length - 1 - currentY] = bright;
  }
}
