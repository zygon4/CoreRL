package com.zygon.rl.game.ui;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.character.Armor;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.character.Equipment;
import com.zygon.rl.world.character.Weapon;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author zygon
 */
public class InventoryRenderer implements GameComponentRenderer {

    private final Layer inventoryLayer;
    private final RenderUtil renderUtil;

    public InventoryRenderer(Layer inventoryLayer, RenderUtil renderUtil) {
        this.inventoryLayer = Objects.requireNonNull(inventoryLayer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
    }

    @Override
    public void clear() {
        inventoryLayer.clear();
    }

    @Override
    public void render(GameState gameState) {

        int yOffset = 1;
        renderUtil.render(inventoryLayer, Position.create(0, yOffset++), "*EQUIPMENT*", Color.yellow);

        CharacterSheet character = gameState.getWorld().getPlayer();
        Equipment eq = character.getEquipment();
        List<Weapon> weapons = eq.getWeapons();

        if (!weapons.isEmpty()) {
            Weapon rWeap = weapons.get(0);
            if (rWeap != null) {
                String txt = "[RIGHT HAND] " + rWeap;
                renderUtil.render(inventoryLayer, Position.create(0, yOffset), txt, Color.MAGENTA);
            }
        }
        yOffset++;

        if (weapons.size() > 1) {
            Weapon lWeap = weapons.get(1);
            if (lWeap != null) {
                String txt = "[LEFT HAND] " + lWeap;
                renderUtil.render(inventoryLayer, Position.create(0, yOffset), txt, Color.MAGENTA);
            }
        }
        yOffset++;

        for (var equipment : character.getEquipment().getEquipmentBySlot().entrySet()) {
            for (Armor armor : equipment.getValue()) {
                String txt = equipment.getKey().getName() + ": " + armor.getName() + " - " + armor.getDescription();
                renderUtil.render(inventoryLayer, Position.create(0, yOffset++), txt, Color.CYAN);
            }
        }

        yOffset++;
        renderUtil.render(inventoryLayer, Position.create(0, yOffset++), "*INVENTORY*", Color.yellow);

        List<String> inv = character.getInventory().getItems().stream()
                .map(item -> item.getName() + ": " + item.getDescription())
                .collect(Collectors.toList());
        for (String item : inv) {
            renderUtil.render(inventoryLayer, Position.create(0, yOffset++), item, Color.WHITE);
        }
    }
}
