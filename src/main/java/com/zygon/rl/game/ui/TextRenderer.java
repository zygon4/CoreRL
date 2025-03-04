/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.ui;

import java.awt.Color;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.zygon.rl.game.GameState;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author zygon
 */
public class TextRenderer implements GameComponentRenderer {

    private static final System.Logger LOGGER = System.getLogger(TextRenderer.class.getCanonicalName());

    private final Layer textLayer;
    private final RenderUtil renderUtil;
    private final Function<GameState, List<String>> getTextFn;

    public TextRenderer(Layer textLayer, RenderUtil renderUtil,
            Function<GameState, List<String>> getTextFn) {
        this.textLayer = Objects.requireNonNull(textLayer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
        this.getTextFn = getTextFn;
    }

    @Override
    public void clear() {
        textLayer.clear();
    }

    @Override
    public void render(GameState gameState) {
        List<String> text = getTextFn.apply(gameState);

        if (text != null) {
            LOGGER.log(Level.INFO, "Rendering text:\n" + text);

            int y = 1;
            for (String t : text) {
                y = render(textLayer, y, renderUtil, t, Color.WHITE);
            }
        }
    }

    public static int render(Layer textLayer, int y, RenderUtil renderUtil,
            String text, Color color) {
        String[] splitText = text.split("\\r?\\n");
        if (splitText != null) {
            for (String st : splitText) {
                renderUtil.render(textLayer, Position.create(0, y++), st, color);
            }
        }
        return y;
    }
}
