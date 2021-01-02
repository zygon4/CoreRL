package com.zygon.rl.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.stewsters.util.shadow.twoDimention.LitMap2d;
import com.stewsters.util.shadow.twoDimention.ShadowCaster2d;
import com.zygon.rl.util.Audio;
import com.zygon.rl.util.NoiseUtil;
import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.CommonAttributes;
import com.zygon.rl.world.DoubleAttribute;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldTile;
import com.zygon.rl.world.character.CharacterSheet;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.hexworks.cobalt.datatypes.Maybe;
import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.ColorThemes;
import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.Functions;
import org.hexworks.zircon.api.SwingApplications;
import org.hexworks.zircon.api.application.AppConfig;
import org.hexworks.zircon.api.behavior.TextOverride;
import org.hexworks.zircon.api.builder.component.ModalBuilder;
import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.ComponentAlignment;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.Fragment;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.component.TextArea;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.component.modal.Modal;
import org.hexworks.zircon.api.component.modal.ModalFragment;
import org.hexworks.zircon.api.component.modal.ModalResult;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.graphics.Layer;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.screen.Screen;
import org.hexworks.zircon.api.uievent.KeyCode;
import org.hexworks.zircon.api.uievent.KeyboardEventType;
import org.hexworks.zircon.api.uievent.MouseEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;
import org.hexworks.zircon.api.view.base.BaseView;
import org.hexworks.zircon.internal.component.modal.EmptyModalResult;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class GameUI {

    private static final System.Logger logger = System.getLogger(GameUI.class.getCanonicalName());

    private static class FOVHelper {

        public static final String VIEW_BLOCK_NAME = CommonAttributes.VIEW_BLOCK.name();

        public float[][] generateSimpleResistances(Location minValues,
                Location maxValues) {

            float[][] portion = new float[maxValues.getX() - minValues.getX()][maxValues.getY() - minValues.getY()];

            for (int y = minValues.getY(); y < maxValues.getY(); y++) {
                for (int x = minValues.getX(); x < maxValues.getX(); x++) {
                    Entity entity = getTerrain(Location.create(x, y));

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
        private final FOVHelper fovHelper = new FOVHelper();

        private Game game;
        private Layer gameScreenLayer = null;
        private SideBar sideBar = null;
        private Layer miniMapLayer = null;
        private boolean initialized = false;

        public GameView(TileGrid tileGrid, ColorTheme colorTheme, Game game) {
            super(tileGrid, colorTheme);

            this.tileGrid = tileGrid;
            this.game = game;
        }

        protected TileGrid getTileGrid() {
            return tileGrid;
        }

        @Override
        public void onDock() {
            super.onDock();

            if (!initialized) {
                Path musicFile = game.getConfiguration().getMusicFile();
                if (musicFile != null) {
                    try {
                        Audio audio = new Audio(musicFile);
                        audio.play();
                    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
                        logger.log(System.Logger.Level.ERROR, "Unable to play music file: " + musicFile, ex);
                    }
                }

                VBox gameScreen = Components.vbox()
                        .withSize(tileGrid.getSize().getWidth() - SIDEBAR_SCREEN_WIDTH, tileGrid.getSize().getHeight() - 3)
                        .withDecorations(org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE))
                        .withAlignmentWithin(tileGrid, ComponentAlignment.TOP_LEFT)
                        .build();
                getScreen().addComponent(gameScreen);

                sideBar = createSideBar(Position.create(gameScreen.getWidth(), 0), game);
                getScreen().addFragment(sideBar);

                miniMapLayer = Layer.newBuilder()
                        .withSize(SIDEBAR_SCREEN_WIDTH, SIDEBAR_SCREEN_WIDTH)
                        .withOffset(gameScreen.getSize().getWidth() + 1, sideBar.getRoot().getHeight() + 1)
                        .build();
                getScreen().addLayer(miniMapLayer);

                gameScreenLayer = Layer.newBuilder()
                        .withSize(gameScreen.getSize())
                        .build();

                getScreen().addLayer(gameScreenLayer);

                updateGameScreen(gameScreenLayer, game);
                updateSideBar(sideBar, game);
                updateMiniMap(miniMapLayer, game);

                Header promptHeader = Components.header()
                        .withSize(20, 1)
                        .withPosition(1, gameScreen.getHeight() + 1)
                        .build();
                getScreen().addComponent(promptHeader);
                promptHeader.setHidden(true);

                tileGrid.processKeyboardEvents(KeyboardEventType.KEY_PRESSED,
                        Functions.fromBiConsumer((event, phase) -> {
                            Input input = Input.valueOf(event.getCode().getCode());

                            long turnStart = System.nanoTime();
                            game = game.turn(input);
                            logger.log(System.Logger.Level.TRACE,
                                    "turn " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - turnStart));

                            long updateGameScreen = System.nanoTime();
                            updateGameScreen(gameScreenLayer, game);
                            logger.log(System.Logger.Level.TRACE,
                                    "screen (ms) " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - updateGameScreen));

                            GameState.InputContext inputCtx = game.getState().getInputContext().peek();
                            promptHeader.setHidden(true);

                            if (inputCtx.getPrompt() != GameState.InputContextPrompt.NONE) {
                                switch (inputCtx.getPrompt()) {
                                    case DIRECTION:
                                        // TODO: modal for direction? I think it's more generic?
                                        promptHeader.setText("Which Direction?");
                                        promptHeader.setHidden(false);
                                        break;
                                    case LIST:
                                        String list = inputCtx.getHandler().getInputs().stream()
                                                .map(i -> i.toString() + ") " + inputCtx.getHandler().getDisplayText(i))
                                                .collect(Collectors.joining("\n"));
                                        promptHeader.setText(list);
                                        promptHeader.setHidden(false);
                                        break;
                                    case MODAL:
                                        replaceWith(new HelpView(this, game));
                                        break;
                                }
                            }

                            updateSideBar(sideBar, game);
                            // I didn't intend to leave this "delay" in here, but it's not a terrible idea..
                            if (game.getState().getTurnCount() % 10 == 0) {
                                updateMiniMap(miniMapLayer, game);
                            }
                        }));
                initialized = true;
            }
        }

        private static final class Dialog implements ModalFragment<ModalResult> {

            private final Screen screen;
            private final Container container;

            public Dialog(Screen screen, Container container) {
                this.screen = screen;
                this.container = container;
            }

            @Override
            public Modal<ModalResult> getRoot() {
                Modal<ModalResult> modal = ModalBuilder.newBuilder()
                        .withComponent(container)
                        .withParentSize(screen.getSize())
                        .build();

                modal.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED,
                        (event, phase) -> {
                            if (event.getCode() == KeyCode.ESCAPE) {
                                modal.close(EmptyModalResult.INSTANCE);
                                return UIEventResponse.processed();
                            } else {
                                return UIEventResponse.pass();
                            }
                        });

                return modal;
            }
        }

        private SideBar createSideBar(Position position, Game game) {

            // TODO: creation method
            Map<String, Component> componentsByName = new LinkedHashMap<>();

            componentsByName.put("world", Components.textArea()
                    .withSize(SIDEBAR_SCREEN_WIDTH - 2, 4)
                    .build());

            componentsByName.put("stats", Components.textArea()
                    .withSize(SIDEBAR_SCREEN_WIDTH - 2, 5)
                    .build());

            componentsByName.put("status", Components.label()
                    .withSize(SIDEBAR_SCREEN_WIDTH - 2, 5)
                    .build());

            String playerName = getPlayer(game).getName();

            return new SideBar(componentsByName,
                    Size.create(SIDEBAR_SCREEN_WIDTH,
                            tileGrid.getSize().getHeight() - SIDEBAR_SCREEN_WIDTH),
                    position,
                    playerName);
        }

        private static int getFoVForce(Game game) {
            // casting is sad but it's safe
            int hour = (int) game.getState().getWorld()
                    .getCalendar().getHourOfDay();
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

        private Entity getPlayer(Game game) {
            return game.getState().getWorld()
                    .get(game.getConfiguration().getPlayerUuid());
        }

        private Location getPlayerLocation(Game game) {
            return getPlayer(game).getLocation();
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

        private void updateSideBar(SideBar sideBar, Game game) {

            Map<String, Component> componentsByName = sideBar.getComponentsByName();

            World world = game.getState().getWorld();
            Entity player = getPlayer(game);
            CharacterSheet playerSheet = CharacterSheet.fromEntity(player);

            String worldText = "Day " + world.getCalendar().getDayOfYear()
                    + ", year " + world.getCalendar().getYear();
            worldText += "\n" + world.getCalendar().getTime()
                    + "  " + world.getCalendar().getSeason().getDisplay();
            ((TextArea) componentsByName.get("world"))
                    .setText(worldText);

            // Gathering these attributes could be expensive.. should only
            // update when needed..
            Set<Attribute> stats = playerSheet.getStats().getAttributes();
            String statsText = stats.stream()
                    .map(attr -> attr.getName() + ": " + attr.getValue())
                    .collect(Collectors.joining("\n"));

            ((TextArea) componentsByName.get("stats"))
                    .setText(statsText);

            String status = playerSheet.getStatus().getEffects()
                    .stream().collect(Collectors.joining(", "));

            ((TextOverride) componentsByName.get("status"))
                    .setText("Age: " + playerSheet.getStatus().getAge() + "    "
                            + "HP: " + playerSheet.getStatus().getHitPoints()
                            + "\n" + status);

        }

        private void updateGameScreen(Layer gameScreen, Game game) {

            Location playerLocation = getPlayerLocation(game);

            int xHalf = gameScreen.getSize().getWidth() / 2;
            int yHalf = gameScreen.getSize().getHeight() / 2;

            // Note these are zero-based
            float[][] lightResistances = fovHelper.generateSimpleResistances(
                    Location.create(playerLocation.getX() - xHalf, playerLocation.getY() - yHalf),
                    Location.create(playerLocation.getX() + xHalf, playerLocation.getY() + yHalf));

            LitMap2d lightMap = new LitMap2DImpl(lightResistances);
            ShadowCaster2d shadowCaster = new ShadowCaster2d(lightMap);

            long fovForce = getFoVForce(game);
            try {
                shadowCaster.recalculateFOV(lightMap.getXSize() / 2, lightMap.getYSize() / 2, fovForce, .5f);
            } catch (java.lang.ArrayIndexOutOfBoundsException aioob) {
                throw new RuntimeException(aioob);
            }

            // zircon is BOTTOM-LEFT oriented
            // starting with 1 because of the border
            for (int y = 1; y < gameScreen.getHeight() - 1; y++) {
                for (int x = 1; x < gameScreen.getWidth() - 1; x++) {

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
                    Location loc = Location.create(getX, getY);

                    Entity npc = getNPC(game, loc);

                    if (locationLightLevelPct > .25) {
                        Entity entity = getTerrain(loc);

                        Maybe<Tile> existingTile = gameScreen.getTileAt(uiScreenPosition);
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
                            gameScreen.draw(bottomTile, uiScreenPosition);
                        }

                        // Not drawing player if they're in a shadow.. will this make sense?
                        Tile topTile = null;
                        if (loc.equals(playerLocation)) {
                            topTile = toTile(Entities.PLAYER);
                        } else {
                            if (npc != null) {
                                topTile = toTile(npc);
                            }
                        }
                        if (topTile != null) {
                            gameScreen.draw(topTile, uiScreenPosition);
                        }
                    } else {
                        gameScreen.draw(BLANK_TILE, uiScreenPosition);
                    }
                }
            }
        }

        private void updateMiniMap(Layer miniMap, Game game) {

            Map<Location, Color> miniMapLocations = createMiniMap(
                    getPlayerLocation(game));

            for (Location loc : miniMapLocations.keySet()) {
                Color color = miniMapLocations.get(loc);
                TileColor tileColor = colorCache.getUnchecked(color);
                Tile tile = BLANK_TILE.createCopy()
                        .withBackgroundColor(tileColor)
                        .withForegroundColor(ANSITileColor.BRIGHT_CYAN);

                Position offset = Position.create(loc.getX(), loc.getY());
                miniMap.draw(tile, offset);
            }
        }
    }

    // this is a specific view
    private static final class TitleView extends BaseView {

        private final TileGrid tileGrid;
        private final Game game;
        private final GameView gameView;

        public TitleView(TileGrid tileGrid, ColorTheme colorTheme, Game game) {
            super(tileGrid, colorTheme);

            this.tileGrid = tileGrid;
            this.game = game;
            this.gameView = new GameView(this.tileGrid, colorTheme, this.game);
        }

        @Override
        public void onDock() {
            super.onDock();

            Panel titleMenuPanel = Components.panel()
                    .withSize(tileGrid.getSize())
                    .withDecorations(
                            org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE,
                                    game.getConfiguration().getGameName()))
                    .build();

            Button startButton = Components.button()
                    .withText("NEW GAME")
                    .withAlignmentWithin(titleMenuPanel, ComponentAlignment.CENTER)
                    .withTileset(CP437TilesetResources.rexPaint16x16())
                    .build();
            startButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
                replaceWith(gameView);
                return UIEventResponse.processed();
            });
            // TODO: store/load game
            Button quitButton = Components.button()
                    .withText("QUIT")
                    .withAlignmentAround(startButton, ComponentAlignment.BOTTOM_CENTER)
                    .withTileset(CP437TilesetResources.rexPaint16x16())
                    .build();
            quitButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
                System.exit(0);
                return UIEventResponse.processed();
            });

            // this is for develop only
            Button pop = Components.button()
                    .withText("POP")
                    .withAlignmentAround(quitButton, ComponentAlignment.BOTTOM_CENTER)
                    .withTileset(CP437TilesetResources.rexPaint16x16())
                    .build();

            pop.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
                Panel modalPanel = Components.panel()
                        .withSize(10, 3)
                        .withAlignmentWithin(tileGrid, ComponentAlignment.CENTER)
                        .withDecorations(
                                org.hexworks.zircon.api.ComponentDecorations.box(BoxType.SINGLE, "moooode"))
                        .build();

                modalPanel.addComponent(Components.label()
                        .withText("foobar")
                        .build());

                getScreen().openModal(new GameView.Dialog(getScreen(), modalPanel));
                return UIEventResponse.processed();
            });

            titleMenuPanel.addComponent(startButton);
            titleMenuPanel.addComponent(quitButton);
            titleMenuPanel.addComponent(pop);
            getScreen().addComponent(titleMenuPanel);
        }
    }

    // Game menu/help view, not associated with the game's input handlers
    // This is similar to the TitleView *for now* but it should evolve
    // TODO: add a 'show commands' button
    private static final class HelpView extends BaseView {

        private final GameView gameView;
        private final Game game;

        public HelpView(GameView gameView, Game game) {
            super(gameView.getTileGrid(), gameView.getTheme());
            this.gameView = gameView;
            this.game = game;
        }

        @Override
        public void onDock() {
            super.onDock();

            Panel titleMenuPanel = Components.panel()
                    .withSize(gameView.getTileGrid().getSize())
                    .withDecorations(
                            org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE,
                                    game.getConfiguration().getGameName()))
                    .build();

            Button continueButton = Components.button()
                    .withText("CONTINUE")
                    .withAlignmentWithin(titleMenuPanel, ComponentAlignment.CENTER)
                    .withTileset(CP437TilesetResources.rexPaint16x16())
                    .build();
            continueButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
                replaceWith(gameView);
                return UIEventResponse.processed();
            });
            // TODO: store/load game
            Button quitButton = Components.button()
                    .withText("QUIT")
                    .withAlignmentAround(continueButton, ComponentAlignment.BOTTOM_CENTER)
                    .withTileset(CP437TilesetResources.rexPaint16x16())
                    .build();
            quitButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
                System.exit(0);
                return UIEventResponse.processed();
            });

            titleMenuPanel.addComponent(continueButton);
            titleMenuPanel.addComponent(quitButton);
            getScreen().addComponent(titleMenuPanel);
        }
    }

    private static final String POWERED_BY = "[powered by https://github.com/zygon4/CoreRL]";

    public void start() {
        //LibgdxApplications
        TileGrid tileGrid = SwingApplications.startTileGrid(
                AppConfig.newBuilder()
                        .withTitle(game.getConfiguration().getGameName() + "      " + POWERED_BY)
                        .withSize(Size.create(80, 60))
                        //                        .withDebugMode(true)
                        //                        .withDebugConfig(DebugConfig.newBuilder().withRelaxBoundsCheck(true).build())
                        .withDefaultTileset(CP437TilesetResources.rexPaint16x16())
                        .build());

        TitleView titleView = new TitleView(tileGrid, ColorThemes.afterDark(), game);

        titleView.dock();
    }

    private static TileColor convert(Color color) {
        return TileColor.create(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private static Map<Location, Color> createMiniMap(Location center) {

        // round
        Location rounded = Location.create(25 * (Math.round(center.getX() / 25)),
                25 * (Math.round(center.getY() / 25)));

        // PERF: could be passed in for performance
        Map<Location, Color> colorsByLocation = new HashMap<>();

        for (int y = rounded.getY() + 200, realY = 0; y > rounded.getY() - 200; y -= 25, realY++) {
            for (int x = rounded.getX() - 200, realX = 0; x < rounded.getX() + 200; x += 25, realX++) {

                Location location = Location.create(x, y);
                Entity entity = getTerrain(location);
                WorldTile wt = WorldTile.get(entity);

                colorsByLocation.put(Location.create(realX, realY), wt.getColor());
            }
        }

        return colorsByLocation;
    }

    // Only used in a single thread
    private static final byte[] NOISE_BYTES = new byte[8];
    private static final NoiseUtil terrainNoise = new NoiseUtil(new Random().nextInt(), 1.0, 1.0);
    private static final NoiseUtil npcNoise = new NoiseUtil(new Random().nextInt(), 1.0, 1.0);

    // TODO: this should be completely customizable via json/config
    // This is also weird because terrain tiles are NOT being set in the world ECS
    // this is just a convenient way to get tile information.
    private static Entity getTerrain(Location location) {
        double terrainVal = terrainNoise.getScaledValue(location.getX(), location.getY());

        ByteBuffer.wrap(NOISE_BYTES).putDouble(terrainVal);
        int noiseFactor = ByteBuffer.wrap(NOISE_BYTES).getInt(4);
        int noise = Math.abs(noiseFactor % 9);

        if (terrainVal < .4) {
            return Entities.PUDDLE;
        } else if (terrainVal < .5) {
            if (noise > 3) {
                return Entities.DIRT;
            } else {
                return Entities.GRASS;
            }
        } else if (terrainVal < .6) {
            if (noise > 4) {
                return Entities.TALL_GRASS;
            } else if (noise > 2) {
                return Entities.TREE;
            } else {
                return Entities.GRASS;
            }
        } else if (terrainVal < .7) {
            if (noise > 3) {
                return Entities.GRASS;
            } else {
                return Entities.TALL_GRASS;
            }
        } else if (terrainVal < .8) {
            if (noise > 6) {
                return Entities.TREE;
            } else {
                return Entities.DIRT;
            }
        } else {
            return Entities.WALL;
        }
    }

    private static Entity getNPC(Game game, Location location) {

        Set<Entity> entities = game.getState().getWorld().getAll(location, null);

        return entities.stream()
                .filter(ent -> ent.getAttribute(CommonAttributes.NPC.name()) != null)
                .findFirst().orElse(null);
    }
}
