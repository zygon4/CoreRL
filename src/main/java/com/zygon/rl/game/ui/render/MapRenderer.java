package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.WorldElement;
import com.zygon.rl.game.GameState;
import com.zygon.rl.util.ColorUtil;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldRegion;

import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.Layer;

/**
 *
 * @author zygon
 */
public class MapRenderer implements GameComponentRenderer {

    private final Layer layer;
    private final RenderUtil renderUtil;

    public MapRenderer(Layer gameScreenLayer, RenderUtil renderUtil) {
        this.layer = Objects.requireNonNull(gameScreenLayer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
    }

    private static final int ROUND_FACTOR = 50;
    private static final int MAP_SIZE_X = 1800;
    private static final int MAP_SIZE_Y = 1100;

    // map with color tiles
    private Map<Location, Color> createMapTiles(World world, Location center) {

        // round
        Location rounded = Location.create(
                ROUND_FACTOR * (Math.round(center.getX() / ROUND_FACTOR)),
                ROUND_FACTOR * (Math.round(center.getY() / ROUND_FACTOR)));

        Set<String> terrainIds = new TreeSet<>();

        // PERF: could be passed in for performance
        Map<Location, Color> colorsByLocation = new HashMap<>();

        for (int y = rounded.getY() + MAP_SIZE_Y, realY = 0; y > rounded.getY() - MAP_SIZE_Y; y -= ROUND_FACTOR, realY++) {
            for (int x = rounded.getX() - MAP_SIZE_X, realX = 0; x < rounded.getX() + MAP_SIZE_X; x += ROUND_FACTOR, realX++) {

                Location location = Location.create(x, y);
                Color color = null;

                if (location.equals(rounded)) {
                    color = ColorUtil.get("GhostWhite");
                } else {
                    WorldRegion region = world.getRegion(location);

                    Terrain terrain = region.getDefaultTerrain();
//                    Terrain terrain = world.getTerrain(location);
                    color = ColorUtil.get(terrain.getColor());
                    terrainIds.add(terrain.getId());
                }

                colorsByLocation.put(Location.create(realX, realY), color);
            }
        }

        System.out.println(terrainIds.stream()
                .collect(Collectors.joining(",")));

        return colorsByLocation;
    }

    private Map<Location, Tile> createMap(World world, Location center) {

        // round
        Location rounded = Location.create(
                ROUND_FACTOR * (Math.round(center.getX() / ROUND_FACTOR)),
                ROUND_FACTOR * (Math.round(center.getY() / ROUND_FACTOR)));

        // PERF: could be passed in for performance
        Map<Location, Tile> colorsByLocation = new HashMap<>();

        for (int y = rounded.getY() + MAP_SIZE_Y, realY = 0; y > rounded.getY() - MAP_SIZE_Y; y -= ROUND_FACTOR, realY++) {
            for (int x = rounded.getX() - MAP_SIZE_X, realX = 0; x < rounded.getX() + MAP_SIZE_X; x += ROUND_FACTOR, realX++) {

                Location location = Location.create(x, y);
                Tile tile = null;

                if (location.equals(rounded)) {
                    tile = RenderUtil.WHITE_TILE;
                } else {
                    tile = toTile(world.getTerrain(location));
                }

                colorsByLocation.put(Location.create(realX, realY), tile);
            }
        }

        return colorsByLocation;
    }

    private final LoadingCache<Color, TileColor> colorCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<Color, TileColor>() {
                @Override
                public TileColor load(Color key) {
                    return RenderUtil.convert(key);
                }
            });

    private void updateMap(Layer mapLayer, GameState gameState) {
        Map<Location, Color> miniMapLocations = createMapTiles(
                gameState.getWorld(), getPlayerLocation(gameState));

        for (Location loc : miniMapLocations.keySet()) {
            Color color = miniMapLocations.get(loc);
            TileColor tileColor = colorCache.getUnchecked(color);
            Tile tile = RenderUtil.BLACK_TILE.createCopy()
                    .withBackgroundColor(tileColor)
                    .withForegroundColor(ANSITileColor.BRIGHT_CYAN);
            Position offset = Position.create(loc.getX(), loc.getY());

            mapLayer.draw(tile, offset);
        }
    }

    @Override
    public void clear() {
        layer.clear();
    }

    @Override
    public void render(GameState gameState) {

        updateMap(layer, gameState);
    }

    private static Location getPlayerLocation(GameState gameState) {
        return gameState.getWorld().getPlayerLocation();
    }

    private Tile toTile(WorldElement element) {
        return renderUtil.toTile(ColorUtil.get(element.getColor()), element.getSymbol().charAt(0));
    }
}
