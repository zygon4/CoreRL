/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.ui;

import java.awt.Color;
import java.util.Objects;
import java.util.Set;

import com.zygon.rl.game.GameState;
import com.zygon.rl.util.quest.QuestInfo;
import com.zygon.rl.world.character.CharacterSheet;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author djc
 */
public class QuestRenderer implements GameComponentRenderer {

    private static final System.Logger LOGGER = System.getLogger(QuestRenderer.class.getCanonicalName());

    private final Layer layer;
    private final RenderUtil renderUtil;

    public QuestRenderer(Layer layer, RenderUtil renderUtil) {
        this.layer = Objects.requireNonNull(layer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
    }

    @Override
    public void clear() {
        layer.clear();
    }

    @Override
    public void render(GameState gameState) {

        renderUtil.fill(layer);

        int yOffset = 1;
        renderUtil.render(layer, Position.create(0, yOffset++), "*QUESTS*", Color.yellow);

        CharacterSheet character = gameState.getWorld().getPlayer();
        Set<QuestInfo> quests = character.getQuests();

        yOffset++;

        for (QuestInfo quest : quests) {
            String txt = quest.getDisplayString(gameState);

            yOffset = TextRenderer.render(layer, yOffset, renderUtil, txt, Color.MAGENTA);
        }
    }
}
