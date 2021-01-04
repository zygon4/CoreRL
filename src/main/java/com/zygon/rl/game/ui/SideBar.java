package com.zygon.rl.game.ui;

import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.Fragment;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.graphics.BoxType;

import java.util.Map;

/**
 *
 * @author zygon
 */
final class SideBar implements Fragment {

    private final VBox root;
    private final Map<String, Component> childrenByName;

    public SideBar(Map<String, Component> components, Size size, Position position, String title) {
        this.root = Components.vbox().withSize(size).withPosition(position).withDecorations(org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE, title)).build();
        childrenByName = components;
        components.keySet().stream().map(k -> components.get(k)).forEach(c -> {
            root.addComponent(c);
        });
    }

    Map<String, Component> getComponentsByName() {
        return childrenByName;
    }

    @Override
    public Component getRoot() {
        return root;
    }

}
