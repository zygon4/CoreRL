package com.zygon.rl.game;

import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.Region;
import com.zygon.rl.world.Regions;
import com.zygon.rl.world.WorldTile;
import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.ColorThemes;
import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.Functions;
import org.hexworks.zircon.api.SwingApplications;
import org.hexworks.zircon.api.application.AppConfig;
import org.hexworks.zircon.api.color.ANSITileColor;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zygon
 */
public class GameUI {

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

        private static final int LEFT_MOUSE = 1;

        private static final int SIDEBAR_SCREEN_WIDTH = 18;

        private static final Tile BLANK_TILE = Tile.newBuilder()
                //                .withBackgroundColor(ANSITileColor.MAGENTA)
                .withForegroundColor(ANSITileColor.RED)
                .withCharacter('@')
                .buildCharacterTile();

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
                        game = game.turn(input);
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

        // TODO: hash Position objects
//        private final Map<Integer, Position> cachedPositions = new HashMap<>();
//
        private void updateGameScreen(Layer gameScreenLayer, Game game) {

            Regions regions = game.getState().getRegions();
            Location playerLocation = regions.find(Entities.PLAYER).iterator().next();
            Region playerRegion = regions.getRegion(playerLocation);

            int xHalf = gameScreenLayer.getSize().getWidth() / 2;
            int yHalf = gameScreenLayer.getSize().getHeight() / 2;

            // starting with 1 because of the border
            for (int y = 1; y < gameScreenLayer.getHeight() - 1; y++) {
                for (int x = 1; x < gameScreenLayer.getWidth() - 1; x++) {

                    Position uiScreenPosition = Position.create(x, y);
                    // TODO: hash positions
//                    int hash = Objects.hash(x, y);
//                    Position uiScreenPosition = cachedPositions.get(hash);
//                    if (uiScreenPosition == null) {
//                        uiScreenPosition = Position.create(x, y);
//                        cachedPositions.put(hash, uiScreenPosition);
//                    }
//                    if (uiScreenPosition.getX() != x || uiScreenPosition.getY() != y) {
//                        throw new IllegalStateException("hash collision");
//                    }

                    // Carve out a viewing "window" in the game region
                    int getX = x > xHalf
                            ? playerLocation.getX() + xHalf - x : playerLocation.getX() - xHalf + x;
                    int getY = y > yHalf
                            ? playerLocation.getY() + yHalf - y : playerLocation.getY() - yHalf + y;

                    Location loc = Location.create(getX, getY);

                    Entity entity = playerRegion.get(loc, 0);
                    WorldTile wt = WorldTile.get(entity);

                    Tile tile = Tile.newBuilder()
                            //                .withBackgroundColor(ANSITileColor.MAGENTA)
                            .withForegroundColor(convert(wt.getColor()))
                            .withCharacter(wt.getGlyph(entity))
                            .buildCharacterTile();

                    gameScreenLayer.draw(tile, uiScreenPosition);
                }
            }

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
        // a TileGrid represents a 2D grid composed of Tiles
        TileGrid tileGrid = SwingApplications.startTileGrid(
                AppConfig.newBuilder()
                        // The number of tiles horizontally, and vertically
                        .withSize(Size.create(80, 60))
                        // You can choose from a wide array of CP437, True Type or Graphical tilesets
                        // which are built into Zircon
                        .withDefaultTileset(CP437TilesetResources.rexPaint16x16())
                        .build());

        TitleView titleView = new TitleView(tileGrid, ColorThemes.afterDark(), game);

        titleView.dock();
    }

    private static TileColor convert(Color color) {
        return TileColor.create(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
