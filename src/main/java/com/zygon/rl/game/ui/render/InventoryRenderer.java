/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.zygon.rl.game.GameState;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author djc
 */
public class InventoryRenderer extends PlayerDataRenderer {

    private static final int INV_OFFSET = 40;
    private static final int Y_START_OFFSET = 1;

    private final Function<GameState, Map<Boolean, List<String>>> getTextFn;

    public InventoryRenderer(Layer inventoryLayer, RenderUtil renderUtil,
            Function<GameState, Map<Boolean, List<String>>> getTextFn) {
        super(inventoryLayer, renderUtil);
        this.getTextFn = getTextFn;
    }

    @Override
    public void render(GameState gameState) {

        getRenderUtil().fill(getLayer());

        int yOffset = Y_START_OFFSET;

        getRenderUtil().render(getLayer(), Position.create(0, yOffset), "*EQUIPMENT*", Color.YELLOW);
        getRenderUtil().render(getLayer(), Position.create(INV_OFFSET, yOffset), "*INVENTORY*", Color.YELLOW);

        Map<Boolean, List<String>> items = getTextFn.apply(gameState);
        if (items != null) {
            List<String> equipped = items.get(Boolean.TRUE);

            // setting to two is weird, make better or explain
            yOffset = Y_START_OFFSET + 1;
            for (String text : equipped) {
                getRenderUtil().render(getLayer(), Position.create(0, yOffset), text, Color.RED);
                yOffset++;
            }

            List<String> inv = items.get(Boolean.FALSE);

            yOffset = Y_START_OFFSET + 1;
            for (String text : inv) {
                getRenderUtil().render(getLayer(), Position.create(INV_OFFSET, yOffset), text, Color.RED);
                yOffset++;
            }
        }
    }
}
