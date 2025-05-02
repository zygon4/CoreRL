/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.List;
import java.util.function.BiFunction;

import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author djc
 */
public class ItemRenderer extends PlayerDataRenderer {

    private static final int Y_START_OFFSET = 1;

    private final GameConfiguration gameConfiguration;
    private final BiFunction<GameConfiguration, GameState, List<String>> getTextFn;

    public ItemRenderer(GameConfiguration gameConfiguration, Layer itemLayer,
            RenderUtil renderUtil,
            BiFunction<GameConfiguration, GameState, List<String>> getTextFn) {
        super(itemLayer, renderUtil);
        this.gameConfiguration = gameConfiguration;
        this.getTextFn = getTextFn;
    }

    @Override
    public void render(GameState gameState) {

        getRenderUtil().fill(getLayer());

        int yOffset = Y_START_OFFSET;
        getRenderUtil().render(getLayer(), Position.create(0, yOffset++), "*ITEM*", Color.yellow);

        List<String> itemText = this.getTextFn.apply(this.gameConfiguration, gameState);
        if (itemText != null) {
            for (String text : itemText) {
                getRenderUtil().render(getLayer(), Position.create(0, yOffset), text, Color.RED);
                yOffset++;
            }
        }
    }
}
