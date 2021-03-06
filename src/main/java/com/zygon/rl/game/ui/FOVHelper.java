package com.zygon.rl.game.ui;

import com.zygon.rl.data.Terrain;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;

/**
 *
 * @author zygon
 */
class FOVHelper {

    public static final String VIEW_BLOCK_NAME = CommonAttributes.VIEW_BLOCK.name();
    private final World world;

    public FOVHelper(World world) {
        this.world = world;
    }

    public float[][] generateSimpleResistances(Location minValues, Location maxValues) {
        float[][] portion = new float[maxValues.getX() - minValues.getX()][maxValues.getY() - minValues.getY()];
        for (int y = minValues.getY(); y < maxValues.getY(); y++) {
            for (int x = minValues.getX(); x < maxValues.getX(); x++) {
                Terrain terrain = world.getTerrain(Location.create(x, y));
                double viewBlocking = getMaxViewBlock(terrain);
                try {
                    portion[x - minValues.getX()][y - minValues.getY()] = (float) viewBlocking;
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return portion;
    }

    public static double getMaxViewBlock(Terrain entity) {
        Double viewBlocker = entity.getFlag(VIEW_BLOCK_NAME);
        return viewBlocker != null ? viewBlocker : 0.0;
    }
}
