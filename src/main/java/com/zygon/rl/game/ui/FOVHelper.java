package com.zygon.rl.game.ui;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;

import java.util.List;

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

                List<Identifable> allElements = world.getAllElements(Location.create(x, y));
                // junk in the way
                double stuffViewBlock = allElements.stream()
                        .map(Identifable::getId)
                        .filter(id -> !id.equals("player"))
                        .map(id -> {
                            Element element = Data.get(id);
                            return element;
                        })
                        .map(FOVHelper::getMaxViewBlock)
                        .mapToDouble(v -> v)
                        .max().orElse(0);

                Terrain terrain = world.getTerrain(Location.create(x, y));
                double terrainViewBlocking = getMaxViewBlock(terrain);

                double maxViewBlock = Math.max(stuffViewBlock, terrainViewBlocking);

                try {
                    portion[x - minValues.getX()][y - minValues.getY()] = (float) maxViewBlock;
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return portion;
    }

    public static double getMaxViewBlock(Element entity) {
        Double viewBlocker = entity.getFlag(VIEW_BLOCK_NAME);
        return viewBlocker != null ? viewBlocker : 0.0;
    }
}
