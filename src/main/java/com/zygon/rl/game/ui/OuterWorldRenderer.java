package com.zygon.rl.game.ui;

import com.google.common.cache.LoadingCache;
import com.stewsters.util.shadow.twoDimention.LitMap2d;
import com.stewsters.util.shadow.twoDimention.ShadowCaster2d;
import com.zygon.rl.data.Element;
import com.zygon.rl.data.Identifable;
import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.TargetingInputHandler;
import com.zygon.rl.util.ColorUtil;
import com.zygon.rl.world.Location;
import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.data.CharacterTile;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.Layer;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author zygon
 */
public class OuterWorldRenderer implements GameComponentRenderer {

    private static final Tile BLACK_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.BLACK)
            .withForegroundColor(ANSITileColor.BLACK)
            .buildCharacterTile();

    // Not too many of these I hope.
    private final Map<Integer, CharacterTile> tileCache = new HashMap<>();

    private final Layer gameScreenLayer;
    private final FOVHelper fovHelper;
    private final LoadingCache<Color, TileColor> colorCache;

    public OuterWorldRenderer(Layer gameScreenLayer, Game game, LoadingCache<Color, TileColor> colorCache) {
        this.gameScreenLayer = Objects.requireNonNull(gameScreenLayer);
        // Just needs world for the noise/terrain function. Would prefer something
        // less heavy.
        this.fovHelper = new FOVHelper(game.getState().getWorld());
        this.colorCache = Objects.requireNonNull(colorCache);
    }

    @Override
    public void clear() {
        gameScreenLayer.clear();
    }

    @Override
    public void render(GameState gameState) {
        Location playerLocation = getPlayerLocation(gameState);
        int xHalf = gameScreenLayer.getSize().getWidth() / 2;
        int yHalf = gameScreenLayer.getSize().getHeight() / 2;

        GameState.InputContext inputCtx = gameState.getInputContext().peek();

        Location target = null;
        Predicate<Location> isOnTargetPath = null;
        if (inputCtx.getName().equals("TARGET")) {
            // Casting is evil but shielding with a Predicate
            TargetingInputHandler lookHandler = (TargetingInputHandler) inputCtx.getHandler();
            target = lookHandler.getTargetLocation();
            isOnTargetPath = (l) -> lookHandler.isOnPath(l);
        }

        // Note these are zero-based
        float[][] lightResistances = fovHelper.generateSimpleResistances(
                Location.create(playerLocation.getX() - xHalf, playerLocation.getY() - yHalf),
                Location.create(playerLocation.getX() + xHalf, playerLocation.getY() + yHalf));
        LitMap2d lightMap = new LitMap2DImpl(lightResistances);
        ShadowCaster2d shadowCaster = new ShadowCaster2d(lightMap);
        long fovForce = getFoVForce(gameState);

        try {
            shadowCaster.recalculateFOV(lightMap.getXSize() / 2, lightMap.getYSize() / 2, fovForce, .5f);
        } catch (java.lang.ArrayIndexOutOfBoundsException aioob) {
            throw new RuntimeException(aioob);
        }
        // zircon is BOTTOM-LEFT oriented
        // starting with 1 because of the border
        for (int y = 1; y < gameScreenLayer.getHeight() - 1; y++) {
            for (int x = 1; x < gameScreenLayer.getWidth() - 1; x++) {
                int getX = playerLocation.getX() - xHalf + x;
                int getY = playerLocation.getY() + yHalf - y;
                double locationLightLevelPct = 1.0;

                try {
                    if (x < lightMap.getXSize() && y < lightMap.getYSize()) {
                        locationLightLevelPct = lightMap.getLight(x, y);
                    }
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    throw new RuntimeException(ex);
                }

                // TODO: simple caching needs performance testing
                Position uiScreenPosition = Position.create(x, y);
                Location loc = Location.create(getX, getY);

                Element actor = gameState.getWorld().get(loc);

                if (locationLightLevelPct > .25) {

                    // Just draw from top to bottom whatever item/actor is available
                    // 1) Draw actor if available
                    if (actor != null) {
                        Tile actorTile = toTile(actor);
                        gameScreenLayer.draw(actorTile, uiScreenPosition);
                    } else {

                        List<Identifable> staticObjectIds = gameState.getWorld().getAll(loc, null);
                        Element object = !staticObjectIds.isEmpty()
                                ? Data.get(staticObjectIds.get(0).getId()) : null;

                        // 2) Next draw item if available
                        if (object != null) {
                            // Would be nice to use sprites eventually..
                            Tile objectTile = toTile(object);
                            gameScreenLayer.draw(objectTile, uiScreenPosition);
                        } else {
                            // 3) Finally draw terrain if nothing is above
                            Terrain terrain = gameState.getWorld().getTerrain(loc);
                            gameScreenLayer.draw(toTile(terrain), uiScreenPosition);
                        }
                    }

                    // Overlay path/targeting, should be a layering/transparent effect
                    Tile targetTile = null;
                    if (target != null && loc.equals(target)) {
                        targetTile = toTile(Color.WHITE, 'O');
                    } else if (isOnTargetPath != null && isOnTargetPath.test(loc)) {
                        targetTile = toTile(Color.WHITE, '#');
                    }
                    if (targetTile != null) {
                        gameScreenLayer.draw(targetTile, uiScreenPosition);
                    }
                } else {
                    gameScreenLayer.draw(BLACK_TILE, uiScreenPosition);
                }
            }
        }
    }

    private static int getFoVForce(GameState gameState) {
        // casting is sad but it's safe
        int hour = (int) gameState.getWorld().getCalendar().getHourOfDay();
        int fov = 0;
        // Can possibly do some clever math to convert the hour so it can
        // be used with a single switch value. Maybe if x > 12 ? 24 - x : x
        switch (hour) {
            case 11, 12 -> {
                fov = 50;
            }
            case 10, 13 -> {
                fov = 50;
            }
            case 9, 14 -> {
                fov = 50;
            }
            case 8, 15 -> {
                fov = 45;
            }
            case 7, 16 -> {
                fov = 40;
            }
            case 6, 17 -> {
                fov = 30;
            }
            case 5, 18 -> {
                fov = 20;
            }
            case 4, 19 -> {
                fov = 15;
            }
            case 3, 20 -> {
                fov = 10;
            }
            case 2, 21 -> {
                fov = 7;
            }
            case 1, 22 -> {
                fov = 5;
            }
            case 0, 23 -> {
                fov = 2;
            }
        }
        // TODO: modified by character stats, status, traits
        return fov;
    }

    private Location getPlayerLocation(GameState gameState) {
        return gameState.getWorld().getPlayerLocation();
    }

    private Tile toTile(Element element) {
        return toTile(ColorUtil.get(element.getColor()), element.getSymbol().charAt(0));
    }

    private CharacterTile toTile(Color color, char symbol) {
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
}
