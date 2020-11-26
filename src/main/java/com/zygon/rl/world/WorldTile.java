package com.zygon.rl.world;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author zygon
 */
public enum WorldTile {

    FLOOR(Entities.FLOOR, (e) -> '.', Color.YELLOW),
    DIRT(Entities.DIRT, (e) -> '.', new Color(109, 57, 4, 1)),
    DOOR(Entities.DOOR, (t) -> {
        Openable openable = new Openable(t);
        return openable.isClosed() ? '+' : '\'';
    }, Color.ORANGE),
    GRASS(Entities.GRASS, (e) -> '.', Color.GREEN),
    TALL_GRASS(Entities.TALL_GRASS, (e) -> '"', Color.GREEN),
    MONSTER(Entities.MONSTER, (e) -> 'm', Color.CYAN),
    PUDDLE(Entities.PUDDLE, (e) -> '~', Color.BLUE),
    PLAYER(Entities.PLAYER, (e) -> '@', Color.MAGENTA),
    ROCK(Entities.ROCK, (e) -> ',', Color.YELLOW),
    TREE(Entities.TREE, (e) -> '4', Color.GREEN),
    WALL(Entities.WALL, (e) -> '#', Color.DARK_GRAY),
    WINDOW(Entities.WINDOW, (t) -> {
        Openable openable = new Openable(t);
        return openable.isClosed() ? '*' : '/';
    }, Color.LIGHT_GRAY);

    private static final Map<String, WorldTile> tilesByEntityName = new HashMap<>();

    static {
        for (WorldTile t : WorldTile.values()) {
            tilesByEntityName.put(t.getEntity().getName(), t);
        }
    }

    private final Entity entity;
    private final Function<Entity, Character> getGlyphFn;
    private final Color color;

    private WorldTile(Entity entity, Function<Entity, Character> getGlyphFn, Color color) {
        this.entity = entity.copy().build();
        this.getGlyphFn = getGlyphFn;
        this.color = color;
    }

    // Also need a getColor(float lightPct)?
    public Color getColor() {
        return color;
    }

    public Entity getEntity() {
        return entity;
    }

    public char getGlyph(Entity entity) {
        return getGlyphFn.apply(entity);
    }

    public static WorldTile get(Entity entity) {
        return tilesByEntityName.get(entity.getName());
    }
}
