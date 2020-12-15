package com.zygon.rl.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.stewsters.util.shadow.twoDimention.LitMap2d;
import com.stewsters.util.shadow.twoDimention.ShadowCaster2d;
import com.zygon.rl.util.Noise;
import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.DoubleAttribute;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.WorldTile;
import org.hexworks.cobalt.datatypes.Maybe;
import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.ColorThemes;
import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.Functions;
import org.hexworks.zircon.api.SwingApplications;
import org.hexworks.zircon.api.application.AppConfig;
import org.hexworks.zircon.api.behavior.TextOverride;
import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.Fragment;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.graphics.Layer;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.uievent.KeyboardEventType;
import org.hexworks.zircon.api.uievent.MouseEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;
import org.hexworks.zircon.api.view.base.BaseView;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zygon
 */
public class GameUI {

    private static final System.Logger logger = System.getLogger(GameUI.class.getCanonicalName());

    private static class FOVHelper {

        public static final String VIEW_BLOCK_NAME = CommonAttributes.VIEW_BLOCK.name();

        public float[][] generateSimpleResistances(Location minValues,
                Location maxValues, Random random) {

            float[][] portion = new float[maxValues.getX() - minValues.getX()][maxValues.getY() - minValues.getY()];

            for (int y = minValues.getY(); y < maxValues.getY(); y++) {
                for (int x = minValues.getX(); x < maxValues.getX(); x++) {
                    Entity entity = getEnity(Location.create(x, y), random);

                    double viewBlocking = getMaxViewBlock(entity);

                    try {
                        portion[x - minValues.getX()][y - minValues.getY()] = (float) viewBlocking;
                    } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            return portion;
        }

        public static double getMaxViewBlock(Entity entity) {
            Attribute viewBlocker = entity.getAttribute(VIEW_BLOCK_NAME);
            return viewBlocker != null ? DoubleAttribute.getValue(viewBlocker) : 0.0;
        }
    }

    // Does this deserve a separate class??
    private static final class LitMap2DImpl implements LitMap2d {

        private final float[][] lightResistances;
        private final float[][] light;

        public LitMap2DImpl(float[][] lightResistances) {
            this.lightResistances = lightResistances;
            this.light = new float[this.lightResistances.length][this.lightResistances[0].length];
        }

        @Override
        public void setLight(int startx, int starty, float force) {
            // Inversed Y
            this.light[startx][this.light[0].length - starty - 1] = force;
        }

        @Override
        public int getXSize() {
            return lightResistances.length;
        }

        @Override
        public int getYSize() {
            return lightResistances[0].length;
        }

        @Override
        public float getLight(int currentX, int currentY) {
            return this.light[currentX][currentY];
        }

        @Override
        public float getResistance(int currentX, int currentY) {
            return lightResistances[currentX][currentY];
        }

        @Override
        public void addLight(int currentX, int currentY, float bright) {
            // Inversed Y because the zircon screen's columns are bottom left, not top left
            this.light[currentX][this.light[0].length - currentY] = bright;
        }
    }

    private final Game game;

    public GameUI(Game game) {
        this.game = game;
    }

    private static final class SideBar implements Fragment {

        private final VBox root;
        private final Map<String, Component> childrenByName;

        public SideBar(Map<String, Component> components, Size size, Position position, String title) {
            this.root = Components.vbox()
                    .withSize(size)
                    .withPosition(position)
                    .withDecorations(
                            org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE, title))
                    .build();

            childrenByName = components;
            components.keySet().stream()
                    .map(k -> components.get(k))
                    .forEach(c -> {
                        root.addComponent(c);
                    });
        }

        private Map<String, Component> getComponentsByName() {
            return childrenByName;
        }

        @Override
        public Component getRoot() {
            return root;
        }
    }

    private static final class GameView extends BaseView {

        private static final int SIDEBAR_SCREEN_WIDTH = 18;

        // TODO: support for different kinds of extra-sensory vision
        private static final Tile BLANK_TILE = Tile.newBuilder()
                .withBackgroundColor(ANSITileColor.BLACK)
                .withForegroundColor(ANSITileColor.BLACK)
                .withCharacter('.')
                .buildCharacterTile();

        private final LoadingCache<Color, TileColor> colorCache
                = CacheBuilder.newBuilder()
                        .maximumSize(100)
                        .build(new CacheLoader<Color, TileColor>() {
                            @Override
                            public TileColor load(Color key) {
                                return convert(key);
                            }
                        });

        private final TileGrid tileGrid;
        private final ColorTheme colorTheme;
        private final Random random;
        private final FOVHelper fovHelper = new FOVHelper();

        private Game game;
        private Layer gameScreenLayer = null;
        private SideBar sideBar = null;

        public GameView(TileGrid tileGrid, ColorTheme colorTheme, Random random, Game game) {
            super(tileGrid, colorTheme);

            this.tileGrid = tileGrid;
            this.colorTheme = colorTheme;
            this.random = random;
            this.game = game;
        }

        @Override
        public void onDock() {
            super.onDock();

            tileGrid.processKeyboardEvents(KeyboardEventType.KEY_PRESSED,
                    Functions.fromBiConsumer((event, phase) -> {
                        System.out.println(event);
                        Input input = Input.valueOf(event.getCode().getCode());

                        long turnStart = System.nanoTime();
                        game = game.turn(input);
                        logger.log(System.Logger.Level.TRACE,
                                "turn " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - turnStart));

                        long updateGameScreen = System.nanoTime();
                        updateGameScreen(gameScreenLayer, game);
                        logger.log(System.Logger.Level.TRACE,
                                "screen (ms) " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - updateGameScreen));

                        updateSideBar(sideBar, game);
                    }));

            VBox gameScreen = Components.vbox()
                    .withSize(tileGrid.getSize().getWidth() - SIDEBAR_SCREEN_WIDTH, tileGrid.getSize().getHeight())
                    .withDecorations(org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE))
                    .build();
            getScreen().addComponent(gameScreen);

            sideBar = createSideBar(Position.create(gameScreen.getWidth(), 0));
            getScreen().addFragment(sideBar);

            gameScreenLayer = Layer.newBuilder()
                    .withSize(gameScreen.getSize())
                    .build();

            getScreen().addLayer(gameScreenLayer);

            updateGameScreen(gameScreenLayer, game);
            updateSideBar(sideBar, game);
        }

        private SideBar createSideBar(Position position) {

            // TODO: creation method
            Map<String, Component> componentsByName = new LinkedHashMap<>();

            componentsByName.put("name", Components.header()
                    .withText("name")
                    .build());

            componentsByName.put("health", Components.label()
                    .withText("health")
                    .build());

            return new SideBar(componentsByName,
                    Size.create(SIDEBAR_SCREEN_WIDTH, tileGrid.getSize().getHeight()),
                    position,
                    game.getConfiguration().getName());
        }

        private void updateSideBar(SideBar sideBar, Game game) {

            Map<String, Component> componentsByName = sideBar.getComponentsByName();
            ((TextOverride) componentsByName.get("name")).setText("NAME!");
            ((TextOverride) componentsByName.get("health")).setText("HEALTH!");
        }

        private void updateGameScreen(Layer gameScreenLayer, Game game) {

            Location playerLocation = game.getState().getPlayerLocation();

            int xHalf = gameScreenLayer.getSize().getWidth() / 2;
            int yHalf = gameScreenLayer.getSize().getHeight() / 2;

            // Note these are zero-based
            float[][] lightResistances = fovHelper.generateSimpleResistances(
                    Location.create(playerLocation.getX() - xHalf, playerLocation.getY() - yHalf),
                    Location.create(playerLocation.getX() + xHalf, playerLocation.getY() + yHalf),
                    random);

            LitMap2d lightMap = new LitMap2DImpl(lightResistances);
            ShadowCaster2d shadowCaster = new ShadowCaster2d(lightMap);

            try {
                shadowCaster.recalculateFOV(lightMap.getXSize() / 2, lightMap.getYSize() / 2, 50, .5f);
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

                    // TODO: hash positions
                    Position uiScreenPosition = Position.create(x, y);

                    if (locationLightLevelPct > .25) {
                        Location loc = Location.create(getX, getY);
                        Entity entity = getEnity(loc, random);

                        Maybe<Tile> existingTile = gameScreenLayer.getTileAt(uiScreenPosition);

                        Tile bottomTile = existingTile.get();
                        boolean drawTile = true;

                        if (bottomTile != null) {
                            String existingTileHash = bottomTile.getCacheKey();
                            bottomTile = toTile(bottomTile, entity);
                            drawTile = !bottomTile.getCacheKey().equals(existingTileHash);
                        } else {
                            bottomTile = toTile(entity);
                        }

                        if (drawTile) {
                            gameScreenLayer.draw(bottomTile, uiScreenPosition);
                        }

                        // Not drawing player if they're in a shadow.. will this make sense?
                        if (loc.equals(playerLocation)) {
                            Tile topTile = toTile(Entities.PLAYER);
                            gameScreenLayer.draw(topTile, uiScreenPosition);
                        }
                    } else {
                        gameScreenLayer.draw(BLANK_TILE, uiScreenPosition);
                    }
                }
            }
        }

        private Tile toTile(Tile tile, Entity entity) {
            WorldTile wt = WorldTile.get(entity);
            return tile.asCharacterTile().get()
                    .withBackgroundColor(colorCache.getUnchecked(Color.BLACK))
                    .withForegroundColor(colorCache.getUnchecked(wt.getColor()))
                    .withCharacter(wt.getGlyph(entity));
        }

        private Tile toTile(Entity entity) {
            WorldTile wt = WorldTile.get(entity);
            return Tile.newBuilder()
                    .withForegroundColor(colorCache.getUnchecked(wt.getColor()))
                    .withCharacter(wt.getGlyph(entity))
                    .buildCharacterTile();
        }
    }

    // this is a specific view
    private static final class TitleView extends BaseView {

        private final TileGrid tileGrid;
        private final ColorTheme colorTheme;
        private final Random random;
        private final Game game;

        public TitleView(TileGrid tileGrid, ColorTheme colorTheme, Random random, Game game) {
            super(tileGrid, colorTheme);

            this.tileGrid = tileGrid;
            this.colorTheme = colorTheme;
            this.random = random;
            this.game = game;
        }

        @Override
        public void onDock() {
            super.onDock();

            Button startButton = Components.button()
                    .withText("NEW GAME")
                    .withTileset(CP437TilesetResources.rexPaint16x16())
                    .build();
            startButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
                replaceWith(new GameView(tileGrid, colorTheme, random, game));
                return UIEventResponse.processed();
            });
            // TODO: store/load game
            Button quitButton = Components.button()
                    .withText("QUIT")
                    .withTileset(CP437TilesetResources.rexPaint16x16())
                    .build();
            quitButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
                System.exit(0);
                return UIEventResponse.processed();
            });

            // This is just a UI example
            AtomicInteger count = new AtomicInteger();
            Button attachment = Components.button()
                    .withText(String.format("Remove: %d", count.get()))
                    .withSize(12, 1)
                    .build();
            attachment.onActivated(org.hexworks.zircon.api.Functions.fromConsumer(
                    (componentEvent -> attachment.setText(String.format("Remove: %d", count.getAndIncrement())))));

            Map<String, Component> startMenuComponents = new LinkedHashMap<>();
            startMenuComponents.put("start", startButton);
            startMenuComponents.put("quit", quitButton);
            startMenuComponents.put("attachment", attachment);

            getScreen().addFragment(
                    new SideBar(startMenuComponents,
                            tileGrid.getSize(), Position.create(0, 0), game.getConfiguration().getName())
            );
        }
    }

    public void start(Random random) {
        //LibgdxApplications
        TileGrid tileGrid = SwingApplications.startTileGrid(
                AppConfig.newBuilder()
                        .withSize(Size.create(80, 60))
                        //                        .withDebugMode(true)
                        //                        .withDebugConfig(DebugConfig.newBuilder().withRelaxBoundsCheck(true).build())
                        .withDefaultTileset(CP437TilesetResources.rexPaint16x16())
                        .build());

        TitleView titleView = new TitleView(tileGrid, ColorThemes.afterDark(), random, game);

        titleView.dock();
    }

    private static TileColor convert(Color color) {
        return TileColor.create(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    // Only used in a single thread
    private static byte[] NOISE_BYTES = new byte[8];

    private static Entity getEnity(Location location, Random random) {
        double terrainVal = Noise.noise(((double) location.getX() / 800l),
                ((double) location.getY() / 800l));

        // First attemp to do this: pull out random ints from the noise value
        // and convert it to a 0-9 to use as an extra noise value.
        ByteBuffer.wrap(NOISE_BYTES).putDouble(terrainVal);
        int noiseFactor = ByteBuffer.wrap(NOISE_BYTES).getInt(4);
        int noise = Math.abs(noiseFactor % 9);

        Entity entity = null;
        if (terrainVal < -0.10) {

            if (noise > 8) {
                entity = Entities.PUDDLE;
            } else if (noise > 3) {
                entity = Entities.DIRT;
            } else {
                entity = Entities.TALL_GRASS;
            }

        } else if (terrainVal < -0.05) {

            if (noise > 7) {
                entity = Entities.DIRT;
            } else if (noise > 3) {
                entity = Entities.GRASS;
            } else {
                entity = Entities.PUDDLE;
            }

        } else if (terrainVal < -0.00) {

            if (noise > 6) {
                entity = Entities.GRASS;
            } else if (noise > 2) {
                entity = Entities.DIRT;
            } else {
                entity = Entities.TALL_GRASS;
            }

        } else if (terrainVal < 0.05) {

            if (noise > 8) {
                entity = Entities.TREE;
            } else if (noise > 5) {
                entity = Entities.TALL_GRASS;
            } else {
                entity = Entities.GRASS;
            }

        } else if (terrainVal < 0.10) {

            if (noise > 8) {
                entity = Entities.TALL_GRASS;
            } else if (noise > 5) {
                entity = Entities.TREE;
            } else {
                entity = Entities.DIRT;
            }

        } else {

            // default to generic "plain" area
            if (noise > 6) {
                entity = Entities.GRASS;
            } else if (noise > 2) {
                entity = Entities.DIRT;
            } else {
                entity = Entities.TALL_GRASS;
            }
        }
        return entity;
    }
}
