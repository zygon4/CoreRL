package com.zygon.rl.game.ui;

import java.awt.Color;
import java.lang.System.Logger.Level;
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

    private static final System.Logger LOGGER = System.getLogger(TextRenderer.class.getCanonicalName());

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

            LOGGER.log(Level.DEBUG, "Rendering note:\n" + note);

            String[] splitText = note.note().split("\\r?\\n");
            if (splitText != null) {
                int y = 1;
                for (String text : splitText) {
                    renderUtil.render(textLayer, Position.create(0, y++), text, Color.WHITE);
                }
            }
        }
    }
}
