package com.zygon.rl.data;

import java.util.Map;

/**
 * A world element is a *template* of anything that could be spawned in the
 * game.
 */
public class WorldElement extends Element {

    // no volume yet
    private final int weight;

    protected WorldElement() {
        super();
        this.weight = 0;
    }

    public WorldElement(String id, String type, String symbol, String color,
            String name, String description, Map<String, Object> flags,
            int weight) {
        super(id, type, symbol, color, name, description, flags);
        this.weight = weight;
    }

    protected WorldElement(WorldElement element) {
        super(element);
        this.weight = element.getWeight();
    }

    @Override
    public boolean isWorldElement() {
        return true;
    }

    public int getWeight() {
        return weight;
    }

    public final WorldElement setWeight(int weight) {
        return new WorldElement(getId(), getType(), getSymbol(), getColor(),
                getName(), getDescription(), getFlags(), weight);
    }
}
