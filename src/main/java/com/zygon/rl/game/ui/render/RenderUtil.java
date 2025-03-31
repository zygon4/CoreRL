package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.CharacterTile;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author zygon
 */
public class RenderUtil {

    public static final Tile BLACK_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.BLACK)
            .withForegroundColor(ANSITileColor.BLACK)
            .buildCharacterTile();
    public static final Tile GRAY_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.GRAY)
            .withForegroundColor(ANSITileColor.GRAY)
            .buildCharacterTile();
    public static final Tile WHITE_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.WHITE)
            .withForegroundColor(ANSITileColor.WHITE)
            .buildCharacterTile();

    private final Map<Integer, CharacterTile> tileCache = new HashMap<>();

    private final LoadingCache<Color, TileColor> colorCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<Color, TileColor>() {
                @Override
                public TileColor load(Color key) {
                    return RenderUtil.convert(key);
                }
            });

    public RenderUtil() {
    }

    public void fill(Layer layer) {
        layer.fill(BLACK_TILE);
    }

    public void render(Layer layer, Position offset, String text, Color color) {

        List<CharacterTile> tiles = toTiles(color, text);
        for (int x = 1, i = 0; i < tiles.size(); x++, i++) {
            Position pos = Position.create(offset.getX() + x, offset.getY());
            layer.draw(tiles.get(i), pos);
        }
    }

    public TileColor getTileColor(Color color) {
        return colorCache.getUnchecked(color);
    }

    public static TileColor convert(Color color) {
        return TileColor.create(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public CharacterTile toTile(Color color, char symbol) {
        //Assuming generating this hash is faster than creating a tile, GC'ing it.
        int hashCode = com.google.common.base.Objects.hashCode(color, symbol);
        CharacterTile cached = tileCache.get(hashCode);

        if (cached != null) {
            return cached;
        }

        CharacterTile tile = Tile.newBuilder()
                .withForegroundColor(getTileColor(color))
                .withCharacter(symbol)
                .buildCharacterTile();

        // Cache tiles
        tileCache.put(hashCode, tile);

        return tile;
    }

    public List<CharacterTile> toTiles(Color color, String text) {
        List<CharacterTile> tiles = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            tiles.add(toTile(color, text.charAt(i)));
        }

        return tiles;
    }
}
