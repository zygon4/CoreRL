/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hexworks.zircon.api.graphics.Layer;

/**
 * Render player data.
 *
 * @author djc
 */
abstract class PlayerDataRenderer implements GameComponentRenderer {

    private final Layer layer;
    private final RenderUtil renderUtil;

    public PlayerDataRenderer(Layer layer, RenderUtil renderUtil) {
        this.layer = Objects.requireNonNull(layer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
    }

    @Override
    public void clear() {
        layer.clear();
    }

    //
    // TODO: a render() common
    //
    protected static record Renderable(int x, int y, String content, Color color) {

    }

    final Layer getLayer() {
        return layer;
    }

    final RenderUtil getRenderUtil() {
        return renderUtil;
    }

    protected static List<Renderable> wrap(int x, int y, String value,
            Color valueColor, String with, Color withColor) {
        List<Renderable> wrapped = new ArrayList<>();

        wrapped.add(new Renderable(x, y, with, withColor));

        int withLength = with.length();
        wrapped.add(new Renderable(x + withLength, y, value, valueColor));

        int textLength = value.length();
        wrapped.add(new Renderable(x + withLength + textLength, y, with, withColor));

        return wrapped;
    }
}
