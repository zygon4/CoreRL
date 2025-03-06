package com.zygon.rl.game.ui;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.stewsters.util.shadow.twoDimention.LitMap2d;
import com.stewsters.util.shadow.twoDimention.ShadowCaster2d;
import com.zygon.rl.data.Terrain;
import com.zygon.rl.data.WorldElement;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.TargetingInputHandler;
import com.zygon.rl.util.ColorUtil;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Tangible;
import com.zygon.rl.world.character.CharacterSheet;

import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.Layer;

/**
 * For overlaying on top of the the main game screen.
 *
 * @author zygon
 */
public class OverlayRenderer implements GameComponentRenderer {

    private final Layer gameScreenLayer;
    private final FOVHelper fovHelper;
    private final RenderUtil renderUtil;

    public OverlayRenderer(Layer gameScreenLayer, Game game,
            RenderUtil renderUtil) {
        this.gameScreenLayer = Objects.requireNonNull(gameScreenLayer);
        // Just needs world for the noise/terrain function. Would prefer something
        // less heavy.
        this.fovHelper = new FOVHelper(game.getState().getWorld());
        this.renderUtil = Objects.requireNonNull(renderUtil);
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

                CharacterSheet actor = gameState.getWorld().get(loc);

                if (locationLightLevelPct > .25) {

                    // Just draw from top to bottom whatever item/actor is available
                    // 1) Draw actor if available
                    if (actor != null) {
                        Tile actorTile = toTile(actor.getTemplate());
                        gameScreenLayer.draw(actorTile, uiScreenPosition);
                    } else {

                        List<Tangible> staticObjectIds = gameState.getWorld().getAll(loc, null);
                        WorldElement object = !staticObjectIds.isEmpty()
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
                        targetTile = renderUtil.toTile(Color.WHITE, 'O');
                    } else if (isOnTargetPath != null && isOnTargetPath.test(loc)) {
                        targetTile = renderUtil.toTile(Color.WHITE, '#');
                    }
                    if (targetTile != null) {
                        gameScreenLayer.draw(targetTile, uiScreenPosition);
                    }
                } else {
                    gameScreenLayer.draw(RenderUtil.BLACK_TILE, uiScreenPosition);
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
                fov = 100;
            }
            case 10, 13 -> {
                fov = 100;
            }
            case 9, 14 -> {
                fov = 100;
            }
            case 8, 15 -> {
                fov = 90;
            }
            case 7, 16 -> {
                fov = 80;
            }
            case 6, 17 -> {
                fov = 60;
            }
            case 5, 18 -> {
                fov = 40;
            }
            case 4, 19 -> {
                fov = 30;
            }
            case 3, 20 -> {
                fov = 20;
            }
            case 2, 21 -> {
                fov = 14;
            }
            case 1, 22 -> {
                fov = 10;
            }
            case 0, 23 -> {
                fov = 4;
            }
        }
        // TODO: modified by character stats, status, traits
        return fov;
    }

    private Location getPlayerLocation(GameState gameState) {
        return gameState.getWorld().getPlayerLocation();
    }

    private Tile toTile(WorldElement element) {
        return renderUtil.toTile(ColorUtil.get(element.getColor()), element.getSymbol().charAt(0));
    }
}
