package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.zygon.rl.game.GameState;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.Layer;

/**
 * This looks a lot like a generic text renderer...
 *
 * @author zygon
 */
public class AbilityRenderer implements GameComponentRenderer {

    private final Layer layer;
    private final RenderUtil renderUtil;
    private final Function<GameState, List<String>> getTextFn;

    public AbilityRenderer(Layer inventoryLayer, RenderUtil renderUtil,
            Function<GameState, List<String>> getTextFn) {
        this.layer = Objects.requireNonNull(inventoryLayer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
        this.getTextFn = getTextFn;
    }

    @Override
    public void clear() {
        layer.clear();
    }

    @Override
    public void render(GameState gameState) {

        renderUtil.fill(layer);

        int yOffset = 1;
        renderUtil.render(layer, Position.create(0, yOffset++), "*ABILITIES*", Color.yellow);

        List<String> abilities = getTextFn.apply(gameState);
        if (abilities != null) {
            for (String text : abilities) {
                renderUtil.render(layer, Position.create(0, yOffset), text, Color.RED);
                yOffset++;
            }
        }
    }
}
