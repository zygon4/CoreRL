/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Proficiency;
import com.zygon.rl.world.character.Stats;
import com.zygon.rl.world.character.Status;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.Layer;

/**
 * Render the player's character sheet.
 *
 * @author djc
 */
public class PlayerRenderer implements GameComponentRenderer {

    private final Layer layer;
    private final RenderUtil renderUtil;

    public PlayerRenderer(Layer layer, RenderUtil renderUtil) {
        this.layer = Objects.requireNonNull(layer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
    }

    @Override
    public void clear() {
        layer.clear();
    }

    // Could become common..
    private static record Renderable(int x, int y, String content, Color color) {

    }

    ;

    @Override
    public void render(GameState gameState) {

        renderUtil.fill(layer);

        CharacterSheet player = gameState.getWorld().getPlayer();

        int xOffset = 0;
        int yOffset = 1;

        for (Renderable renderable : getStatsText(xOffset, yOffset, player.getModifiedStats())) {
            renderUtil.render(layer, Position.create(renderable.x(), renderable.y()),
                    renderable.content(), renderable.color());
        }

        xOffset = 40;
        for (Renderable renderable : getProficiencyText(xOffset, yOffset, player.getProficiencies())) {
            renderUtil.render(layer, Position.create(renderable.x(), renderable.y()),
                    renderable.content(), renderable.color());
        }

        xOffset = 0;
        yOffset = 10;
        for (Renderable renderable : getGeneralText(xOffset, yOffset, player)) {
            renderUtil.render(layer, Position.create(renderable.x(), renderable.y()),
                    renderable.content(), renderable.color());
        }

        xOffset = 40;
        yOffset = 10;
        for (Renderable renderable : getStatusText(xOffset, yOffset, player.getStatus())) {
            renderUtil.render(layer, Position.create(renderable.x(), renderable.y()),
                    renderable.content(), renderable.color());
        }
    }

    Collection<Renderable> getStatsText(final int baseX, final int baseY,
            Stats stats) {

        Set<Renderable> renders = new HashSet<>();

        // Attributes are more generic, used for UI/UX
        Set<Attribute> attributes = stats.getAttributes();

        int x = baseX;
        int y = baseY;

        for (var attr : attributes) {
            renders.add(new Renderable(x, y, attr.getName(), Color.GRAY));
            renders.add(new Renderable(x + 5, y, attr.getValue(), Color.BLUE));
            y++;
        }

        return renders;
    }

    Collection<Renderable> getProficiencyText(final int baseX, final int baseY,
            Set<Proficiency> proficiencies) {

        Set<Renderable> renders = new HashSet<>();

        // Attributes are more generic, used for UI/UX
        Set<Attribute> attributes = proficiencies.stream()
                .map(Proficiency::getAttribute)
                .collect(Collectors.toSet());

        int x = baseX;
        int y = baseY;

        for (var attr : attributes) {
            renders.add(new Renderable(x, y, attr.getName(), Color.GRAY));
            renders.add(new Renderable(x + 15, y, attr.getValue(), Color.RED));
            y++;
        }

        return renders;
    }

    Collection<Renderable> getGeneralText(final int baseX, final int baseY,
            CharacterSheet player) {

        Set<Renderable> renders = new HashSet<>();

        int x = baseX;
        int y = baseY;

        renders.add(new Renderable(x, y++, "Species: " + player.getSpecies(), Color.GRAY));
        renders.add(new Renderable(x, y++, "Weight:  " + player.getWeight(), Color.GRAY));
        renders.add(new Renderable(x, y++, "Age:     " + player.getStatus().getAge(), Color.GRAY));
        y++;// space
        renders.add(new Renderable(x, y++, "HP:      " + player.getStatus().getHitPoints(), Color.GRAY));

        return renders;
    }

    Collection<Renderable> getStatusText(final int baseX, final int baseY,
            Status status) {

        Set<Renderable> renders = new HashSet<>();

        // Attributes are more generic, used for UI/UX
        Set<Attribute> attributes = status.getEffectAttributes();

        int x = baseX;
        int y = baseY;

        for (var attr : attributes) {
            renders.add(new Renderable(x, y, attr.getName(), Color.GRAY));
            renders.add(new Renderable(x + 10, y, attr.getValue(), Color.YELLOW));
            y++;
        }

        return renders;
    }
}
