/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.zygon.rl.game.GameState;
import com.zygon.rl.util.StringUtil;
import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Proficiency;
import com.zygon.rl.world.character.ProficiencyProgress;
import com.zygon.rl.world.character.Progress;
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

    @Override
    public void render(GameState gameState) {

        renderUtil.fill(layer);

        CharacterSheet player = gameState.getWorld().getPlayer();

        int xOffset = 0;
        int yOffset = 1;

        for (Renderable renderable : getStatsText(xOffset, yOffset, player.getStats(), player.getModifiedStats())) {
            renderUtil.render(layer, Position.create(renderable.x(), renderable.y()),
                    renderable.content(), renderable.color());
        }

        xOffset = 40;
        for (Renderable renderable : getProficiencyText(xOffset, yOffset,
                player.getProficiencies(), player.getProgress())) {
            renderUtil.render(layer, Position.create(renderable.x(), renderable.y()),
                    renderable.content(), renderable.color());
        }

        xOffset = 0;
        yOffset = 18;
        for (Renderable renderable : getGeneralText(xOffset, yOffset, player)) {
            renderUtil.render(layer, Position.create(renderable.x(), renderable.y()),
                    renderable.content(), renderable.color());
        }

        xOffset = 40;
        yOffset = 18;
        for (Renderable renderable : getStatusText(xOffset, yOffset, player.getStatus())) {
            renderUtil.render(layer, Position.create(renderable.x(), renderable.y()),
                    renderable.content(), renderable.color());
        }
    }

    Collection<Renderable> getStatsText(final int baseX, final int baseY,
            Stats stats, Stats modifiedStats) {

        Set<Renderable> renders = new HashSet<>();

        // Attributes are more generic, used for UI/UX
        Set<Attribute> modifiedAttributes = modifiedStats.getAttributes();
        Map<String, Attribute> baseStatsByAttrName = stats.getAttributes().stream()
                .collect(Collectors.toMap(Attribute::getName, v -> v));

        int x = baseX;
        int y = baseY;

        for (var attr : modifiedAttributes) {
            renders.add(new Renderable(x, y, attr.getName(), Color.GRAY));
            renders.add(new Renderable(x + 5, y, attr.getValue(), Color.BLUE));

            Attribute base = baseStatsByAttrName.get(attr.getName());
            renders.addAll(wrap(x + 10, y, base.getValue(), Color.RED, "|", Color.GRAY));
            y++;
        }

        return renders;
    }

    Collection<Renderable> getProficiencyText(final int baseX, final int baseY,
            Set<Proficiency> proficiencies, Progress progress) {

        Set<Renderable> renders = new HashSet<>();

        // Attributes are more generic, used for UI/UX
        Map<String, Attribute> attributes = proficiencies.stream()
                .collect(Collectors.toMap(
                        k -> k.getProficiency().getId(),
                        v -> v.getAttribute()));

        int x = baseX;
        int y = baseY;

        renders.add(new Renderable(x, y++, "Proficiencies", Color.GRAY));
        y++;

        for (String key : attributes.keySet()) {
            Attribute attr = attributes.get(key);
            renders.add(new Renderable(x, y, attr.getName(), Color.GRAY));
            renders.add(new Renderable(x + 15, y, attr.getValue(), Color.RED));

            ProficiencyProgress proficiencyProgress = progress.getProficiencyProgress(key);
            if (proficiencyProgress != null) {
                String val = proficiencyProgress.getXp() + "/" + proficiencyProgress.getRequiredXp();
                renders.add(new Renderable(x + 20, y, val, Color.BLUE));
            }

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

        Status status = player.getStatus();
        for (String poolName : status.getPoolNames()) {
            Attribute poolAttr = status.getPool(poolName).getAttribute();
            String paddedName = StringUtil.padEnd(poolAttr.getName() + ":", 9);
            renders.add(new Renderable(x, y++, paddedName + poolAttr.getValue(), Color.GRAY));
        }

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

    private List<Renderable> wrap(int x, int y, String value, Color valueColor,
            String with, Color withColor) {
        List<Renderable> wrapped = new ArrayList<>();

        wrapped.add(new Renderable(x, y, with, withColor));

        int withLength = with.length();
        wrapped.add(new Renderable(x + withLength, y, value, valueColor));

        int textLength = value.length();
        wrapped.add(new Renderable(x + withLength + textLength, y, with, withColor));

        return wrapped;
    }
}
