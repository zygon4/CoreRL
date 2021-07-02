package com.zygon.rl.game.ui;

import com.google.common.cache.LoadingCache;
import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.CharacterTile;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.Layer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author zygon
 */
public class RenderUtil {

    public static final Tile BLACK_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.BLACK)
            .withForegroundColor(ANSITileColor.BLACK)
            .buildCharacterTile();
    public static final Tile WHITE_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.WHITE)
            .withForegroundColor(ANSITileColor.WHITE)
            .buildCharacterTile();

    private final Map<Integer, CharacterTile> tileCache = new HashMap<>();

    private final LoadingCache<Color, TileColor> colorCache;

    public RenderUtil(LoadingCache<Color, TileColor> colorCache) {
        this.colorCache = Objects.requireNonNull(colorCache);
    }

    public void render(Layer layer, Position offset, String text, Color color) {

        List<CharacterTile> fooBar = toTiles(color, text);
        for (int x = 1, i = 0; i < fooBar.size(); x++, i++) {
            Position pos = Position.create(offset.getX() + x, offset.getY());
            layer.draw(fooBar.get(i), pos);
        }
    }

    public CharacterTile toTile(Color color, char symbol) {
        //Assuming generating this hash is faster than creating a tile, GC'ing it.
        int hashCode = com.google.common.base.Objects.hashCode(color, symbol);
        CharacterTile cached = tileCache.get(hashCode);

        if (cached != null) {
            return cached;
        }

        CharacterTile tile = Tile.newBuilder()
                .withForegroundColor(colorCache.getUnchecked(color))
                .withCharacter(symbol)
                .buildCharacterTile();

        // Cache tiles
        tileCache.put(hashCode, tile);

        return tile;
    }

    public List<CharacterTile> toTiles(Color color, String foo) {
        List<CharacterTile> tiles = new ArrayList<>();

        for (int i = 0; i < foo.length(); i++) {
            tiles.add(toTile(color, foo.charAt(i)));
        }

        return tiles;
    }
}
