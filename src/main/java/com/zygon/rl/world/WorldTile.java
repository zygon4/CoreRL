package com.zygon.rl.world;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * TODO: convert to json templating.
 *
 */
public enum WorldTile {

    FLOOR(Entities.FLOOR, '.', Color.YELLOW),
    DOOR(Entities.DOOR, ' ', (t) -> {
        Openable openable = new Openable(t);
        return openable.isClosed() ? '+' : '\'';
    }, Color.ORANGE),
    MONSTER(Entities.MONSTER, ' ', (t) -> {
        return t.hasAttribute(CommonAttributes.NPC.name()) ? 'p' : 'm';
    }, Color.CYAN),
    PLAYER(Entities.PLAYER, '@', Color.MAGENTA),
    ROCK(Entities.ROCK, ',', Color.YELLOW),
    WINDOW(Entities.WINDOW, ' ', (t) -> {
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
    private final char glyph;
    private final Function<Entity, Character> getGlyphFn;
    private final Color color;

    private WorldTile(Entity entity, char glyph, Function<Entity, Character> getGlyphFn, Color color) {
        this.entity = entity.copy().build();
        this.glyph = glyph;
        this.getGlyphFn = getGlyphFn;
        this.color = color;
    }

    private WorldTile(Entity entity, char glyph, Color color) {
        this(entity, glyph, null, color);
    }

    // Also need a getColor(float lightPct)?
    public Color getColor() {
        return color;
    }

    public Entity getEntity() {
        return entity;
    }

    public char getGlyph(Entity entity) {
        return getGlyphFn == null
                ? glyph : getGlyphFn.apply(entity).charValue();
    }

    public static WorldTile get(String entityName) {
        return tilesByEntityName.get(entityName);
    }

    public static WorldTile get(Entity entity) {
        return get(entity.getName());
    }
}
