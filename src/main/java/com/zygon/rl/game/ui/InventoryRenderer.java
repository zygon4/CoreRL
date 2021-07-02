package com.zygon.rl.game.ui;

import com.zygon.rl.game.GameState;
import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author zygon
 */
public class InventoryRenderer implements GameComponentRenderer {

    private static final Tile WHITE_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.WHITE)
            .withForegroundColor(ANSITileColor.WHITE)
            .buildCharacterTile();

    private final Layer inventoryLayer;

    public InventoryRenderer(Layer inventoryLayer) {
        this.inventoryLayer = inventoryLayer;
    }

    @Override
    public void clear() {
        inventoryLayer.clear();
    }

    @Override
    public void render(GameState gameState) {

        for (int y = 1; y < inventoryLayer.getHeight() - 1; y++) {
            for (int x = 1; x < inventoryLayer.getWidth() - 1; x++) {
                Position uiScreenPosition = Position.create(x, y);
                inventoryLayer.draw(WHITE_TILE, uiScreenPosition);
            }
        }
    }
}
