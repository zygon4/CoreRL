package com.zygon.rl.game.ui;

import java.awt.Color;
import java.util.Objects;

import com.zygon.rl.game.GameState;
import com.zygon.rl.game.Notification;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author zygon
 */
public class TextRenderer implements GameComponentRenderer {

    private final Layer textLayer;
    private final RenderUtil renderUtil;

    public TextRenderer(Layer textLayer, RenderUtil renderUtil) {
        this.textLayer = Objects.requireNonNull(textLayer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
    }

    @Override
    public void clear() {
        textLayer.clear();
    }

    @Override
    public void render(GameState gameState) {
        Notification note = gameState.getNotification();
        if (note != null) {
            renderUtil.render(textLayer, Position.create(0, 1), note.note(), Color.WHITE);
        }
    }
}
