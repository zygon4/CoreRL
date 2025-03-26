package com.zygon.rl.game.ui.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.zygon.rl.data.Terrain;
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

    private static final int MINIMAP_ROUND_FACTOR = 25;
    private static final int MAP_ROUND_FACTOR = 50;
    private static final int MAP_SIZE_X = 1800;
    private static final int MAP_SIZE_Y = 1100;

    private final Layer layer;
    private final RenderUtil renderUtil;

    public MapRenderer(Layer gameScreenLayer, RenderUtil renderUtil) {
        this.layer = Objects.requireNonNull(gameScreenLayer);
        this.renderUtil = Objects.requireNonNull(renderUtil);
    }

    @Override
    public void clear() {
        layer.clear();
    }

    @Override
    public void render(GameState gameState) {
        updateMap(layer, gameState);
    }

    // map with color tiles
    private Map<Location, Color> createMapTiles(World world, Location center) {

        // round
        Location rounded = center.round(MAP_ROUND_FACTOR);

        // PERF: could be passed in for performance
        Map<Location, Color> colorsByLocation = new HashMap<>();

        for (int y = rounded.getY() + MAP_SIZE_Y, realY = 0; y > rounded.getY() - MAP_SIZE_Y; y -= MAP_ROUND_FACTOR, realY++) {
            for (int x = rounded.getX() - MAP_SIZE_X, realX = 0; x < rounded.getX() + MAP_SIZE_X; x += MAP_ROUND_FACTOR, realX++) {

                Location location = Location.create(x, y);
                Color color = null;

                if (location.equals(rounded)) {
                    color = ColorUtil.get("GhostWhite");
                } else {
                    WorldRegion region = world.getRegion(location);
                    Terrain terrain = region.getDefaultTerrain();
                    color = ColorUtil.get(terrain.getColor());
                }

                colorsByLocation.put(Location.create(realX, realY), color);
            }
        }

        return colorsByLocation;
    }

    private void updateMap(Layer mapLayer, GameState gameState) {
        Map<Location, Color> miniMapLocations = createMapTiles(
                gameState.getWorld(), getPlayerLocation(gameState));

        for (Location loc : miniMapLocations.keySet()) {
            Color color = miniMapLocations.get(loc);
            TileColor tileColor = renderUtil.getTileColor(color);
            Tile tile = RenderUtil.BLACK_TILE.createCopy()
                    .withBackgroundColor(tileColor)
                    .withForegroundColor(ANSITileColor.BRIGHT_CYAN);
            Position offset = Position.create(loc.getX(), loc.getY());

            mapLayer.draw(tile, offset);
        }
    }

    private static Location getPlayerLocation(GameState gameState) {
        return gameState.getWorld().getPlayerLocation();
    }

    // Mini-map functions:
    private static Map<Location, Color> createMiniMap(World world,
            Location center) {

        // round
        Location rounded = center.round(MINIMAP_ROUND_FACTOR);

        // PERF: could be passed in for performance
        Map<Location, Color> colorsByLocation = new HashMap<>();

        for (int y = rounded.getY() + 200, realY = 0; y > rounded.getY() - 200; y -= MINIMAP_ROUND_FACTOR, realY++) {
            for (int x = rounded.getX() - 200, realX = 0; x < rounded.getX() + 200; x += MINIMAP_ROUND_FACTOR, realX++) {

                Location location = Location.create(x, y);
                Terrain terrain = world.getTerrain(location);
                Color color = ColorUtil.get(terrain.getColor());
                colorsByLocation.put(Location.create(realX, realY), color);
            }
        }

        return colorsByLocation;
    }

    public static void updateMiniMap(Layer miniMap, GameState gameState,
            RenderUtil renderUtil) {
        Map<Location, Color> miniMapLocations = createMiniMap(
                gameState.getWorld(), gameState.getWorld().getPlayerLocation());
        for (Location loc : miniMapLocations.keySet()) {
            Color color = miniMapLocations.get(loc);
            TileColor tileColor = renderUtil.getTileColor(color);
            Tile tile = RenderUtil.BLACK_TILE.createCopy()
                    .withBackgroundColor(tileColor)
                    .withForegroundColor(ANSITileColor.BRIGHT_CYAN);
            Position offset = Position.create(loc.getX(), loc.getY());

            miniMap.draw(tile, offset);
        }
    }
}
