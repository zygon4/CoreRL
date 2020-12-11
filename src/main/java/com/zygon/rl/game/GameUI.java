package com.zygon.rl.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Regions;
import com.zygon.rl.world.WorldTile;
import org.hexworks.cobalt.datatypes.Maybe;
import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.ColorThemes;
import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.Functions;
import org.hexworks.zircon.api.SwingApplications;
import org.hexworks.zircon.api.application.AppConfig;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.Fragment;
import org.hexworks.zircon.api.component.TextArea;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zygon
 */
public class GameUI {

    private static final System.Logger logger = System.getLogger(GameUI.class.getCanonicalName());

    private final Game game;

    public GameUI(Game game) {
        this.game = game;
    }

    private static final class Sidebar implements Fragment {

        private final VBox root;

        public Sidebar(List<Component> components, Size size, Position position, String title) {
            this.root = Components.vbox()
                    .withSize(size)
                    .withPosition(position)
                    .withDecorations(
                            org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE, title))
                    .build();

            components.forEach(root::addComponent);
        }

        @Override
        public Component getRoot() {
            return root;
        }
    }

    private static final class GameView extends BaseView {

        private static final int SIDEBAR_SCREEN_WIDTH = 18;

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
        private Game game;

        private TextArea playerInfo = null;
        private Layer gameScreenLayer = null;

        public GameView(TileGrid tileGrid, ColorTheme colorTheme, Game game) {
            super(tileGrid, colorTheme);

            this.tileGrid = tileGrid;
            this.colorTheme = colorTheme;
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
                    }));

            VBox gameScreen = Components.vbox()
                    .withSize(tileGrid.getSize().getWidth() - SIDEBAR_SCREEN_WIDTH, tileGrid.getSize().getHeight())
                    .withDecorations(org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE))
                    .build();

            getScreen().addComponent(gameScreen);

            // TODO: sidebar info
            gameScreenLayer = Layer.newBuilder()
                    .withSize(gameScreen.getSize())
                    .build();

            getScreen().addLayer(gameScreenLayer);

            updateGameScreen(gameScreenLayer, game);
        }

        private void updateGameScreen(Layer gameScreenLayer, Game game) {

            Regions regions = game.getState().getRegions();
            Location playerLocation = regions.find(Entities.PLAYER).iterator().next();

            int xHalf = gameScreenLayer.getSize().getWidth() / 2;
            int yHalf = gameScreenLayer.getSize().getHeight() / 2;

            // zircon is BOTTOM-LEFT oriented
            // starting with 1 because of the border
            for (int y = 1; y < gameScreenLayer.getHeight() - 1; y++) {
                for (int x = 1; x < gameScreenLayer.getWidth() - 1; x++) {

                    int getX = playerLocation.getX() - xHalf + x;
                    int getY = playerLocation.getY() + yHalf - y;

                    Location loc = Location.create(getX, getY);

                    List<Entity> entity = regions.get(loc);

                    if (entity.isEmpty()) {
                        throw new IllegalStateException(loc.toString());
                    }

                    Entity bottom = entity.get(0);

                    // TODO: hash positions
                    Position uiScreenPosition = Position.create(x, y);

                    Maybe<Tile> existingTile = gameScreenLayer.getTileAt(uiScreenPosition);

                    Tile bottomTile = existingTile.get();
                    boolean drawTile = true;

                    if (bottomTile != null) {
                        String existingTileHash = bottomTile.getCacheKey();
                        bottomTile = toTile(bottomTile, bottom);
                        drawTile = !bottomTile.getCacheKey().equals(existingTileHash);
                    } else {
                        bottomTile = toTile(bottom);
                    }

                    if (drawTile) {
                        gameScreenLayer.draw(bottomTile, uiScreenPosition);
                    }

                    if (entity.size() > 1) {
                        Entity top = (entity.get(entity.size() - 1));
                        Tile topTile = toTile(top);
                        gameScreenLayer.draw(topTile, uiScreenPosition);
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
        private final Game game;

        public TitleView(TileGrid tileGrid, ColorTheme colorTheme, Game gameUI) {
            super(tileGrid, colorTheme);

            this.tileGrid = tileGrid;
            this.colorTheme = colorTheme;
            this.game = gameUI;
        }

        @Override
        public void onDock() {
            super.onDock();

            Button startButton = Components.button()
                    .withText("NEW GAME")
                    .withTileset(CP437TilesetResources.rexPaint16x16())
                    .build();
            startButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
//                game.start();
                replaceWith(new GameView(tileGrid, colorTheme, game));
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

            getScreen().addFragment(
                    new Sidebar(List.of(startButton, quitButton, attachment),
                            tileGrid.getSize(), Position.create(0, 0), game.getConfiguration().getName()));
        }
    }

    public void start() {
        //LibgdxApplications
        TileGrid tileGrid = SwingApplications.startTileGrid(
                AppConfig.newBuilder()
                        .withSize(Size.create(80, 60))
                        .withDefaultTileset(CP437TilesetResources.rexPaint16x16())
                        .build());

        TitleView titleView = new TitleView(tileGrid, ColorThemes.afterDark(), game);

        titleView.dock();
    }

    private static TileColor convert(Color color) {
        return TileColor.create(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
