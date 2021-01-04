package com.zygon.rl.game.ui;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.stewsters.util.shadow.twoDimention.LitMap2d;
import com.stewsters.util.shadow.twoDimention.ShadowCaster2d;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.Input;
import com.zygon.rl.util.Audio;
import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.Entities;
import com.zygon.rl.world.Entity;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.World;
import com.zygon.rl.world.WorldTile;
import com.zygon.rl.world.character.CharacterSheet;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.hexworks.cobalt.datatypes.Maybe;
import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.Functions;
import org.hexworks.zircon.api.behavior.TextOverride;
import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.ComponentAlignment;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.LogArea;
import org.hexworks.zircon.api.component.TextArea;
import org.hexworks.zircon.api.component.VBox;
import org.hexworks.zircon.api.data.Position;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.data.Tile;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.graphics.Layer;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.uievent.KeyboardEventType;
import org.hexworks.zircon.api.view.base.BaseView;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
final class GameView extends BaseView {

    private static final System.Logger logger = System.getLogger(GameView.class.getCanonicalName());

    private static final int SIDEBAR_SCREEN_WIDTH = 18;
    // TODO: support for different kinds of extra-sensory vision
    private static final Tile BLANK_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.BLACK)
            .withForegroundColor(ANSITileColor.BLACK)
            .buildCharacterTile();
    private final LoadingCache<Color, TileColor> colorCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<Color, TileColor>() {
                @Override
                public TileColor load(Color key) {
                    return GameUI.convert(key);
                }
            });
    private final TileGrid tileGrid;
    private final FOVHelper fovHelper;
    private Game game;
    private Layer gameScreenLayer = null;
    private SideBar sideBar = null;
    private Layer miniMapLayer = null;
    private boolean initialized = false;

    public GameView(TileGrid tileGrid, ColorTheme colorTheme, Game game) {
        super(tileGrid, colorTheme);
        this.tileGrid = tileGrid;
        this.fovHelper = new FOVHelper(game.getState().getWorld());
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

            VBox gameScreen = Components.vbox().withSize(tileGrid.getSize().getWidth() - SIDEBAR_SCREEN_WIDTH, tileGrid.getSize().getHeight() - 3).withDecorations(org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE)).withAlignmentWithin(tileGrid, ComponentAlignment.TOP_LEFT).build();
            getScreen().addComponent(gameScreen);

            sideBar = createSideBar(Position.create(gameScreen.getWidth(), 0), game);
            getScreen().addFragment(sideBar);

            miniMapLayer = Layer.newBuilder().withSize(SIDEBAR_SCREEN_WIDTH, SIDEBAR_SCREEN_WIDTH).withOffset(gameScreen.getSize().getWidth() + 1, sideBar.getRoot().getHeight() + 1).build();
            getScreen().addLayer(miniMapLayer);

            gameScreenLayer = Layer.newBuilder().withSize(gameScreen.getSize()).build();
            getScreen().addLayer(gameScreenLayer);

            updateGameScreen(gameScreenLayer, game);
            updateSideBar(sideBar, game);
            updateMiniMap(miniMapLayer, game);

            Header promptHeader = Components.header().withSize(20, 1).withPosition(1, gameScreen.getHeight() + 1).build();
            getScreen().addComponent(promptHeader);
            promptHeader.setHidden(true);
            tileGrid.processKeyboardEvents(KeyboardEventType.KEY_PRESSED, Functions.fromBiConsumer((event, phase) -> {
                Input input = Input.valueOf(event.getCode().getCode());

                long turnStart = System.nanoTime();
                game = game.turn(input);
                logger.log(System.Logger.Level.TRACE, "turn (ms) " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - turnStart));

                long updateGameScreen = System.nanoTime();
                updateGameScreen(gameScreenLayer, game);
                logger.log(System.Logger.Level.TRACE, "game screen (ms) " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - updateGameScreen));

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
                            String list = inputCtx.getHandler().getInputs().stream().map(i -> i.toString() + ") " + inputCtx.getHandler().getDisplayText(i)).collect(Collectors.joining("\n"));
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

    private SideBar createSideBar(Position position, Game game) {
        // TODO: creation method
        Map<String, Component> componentsByName = new LinkedHashMap<>();
        componentsByName.put("world", Components.textArea().withSize(SIDEBAR_SCREEN_WIDTH - 2, 4).build());
        componentsByName.put("stats", Components.textArea().withSize(SIDEBAR_SCREEN_WIDTH - 2, 5).build());
        componentsByName.put("status", Components.label().withSize(SIDEBAR_SCREEN_WIDTH - 2, 5).build());
        // TODO: full screen log area (view?) to see/search all
        LogArea logArea = Components.logArea().withSize(SIDEBAR_SCREEN_WIDTH - 2, 20).withLogRowHistorySize(5).build();
        componentsByName.put("log", logArea);

        String playerName = getPlayer(game).getName();
        return new SideBar(componentsByName, Size.create(SIDEBAR_SCREEN_WIDTH, tileGrid.getSize().getHeight() - SIDEBAR_SCREEN_WIDTH), position, playerName);
    }

    private static int getFoVForce(Game game) {
        // casting is sad but it's safe
        int hour = (int) game.getState().getWorld().getCalendar().getHourOfDay();
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
        return game.getState().getWorld().get(game.getConfiguration().getPlayerUuid());
    }

    private Location getPlayerLocation(Game game) {
        return getPlayer(game).getLocation();
    }

    private Tile toTile(Tile tile, Entity entity) {
        WorldTile wt = WorldTile.get(entity);
        return tile.asCharacterTile().get().withBackgroundColor(colorCache.getUnchecked(Color.BLACK)).withForegroundColor(colorCache.getUnchecked(wt.getColor())).withCharacter(wt.getGlyph(entity));
    }

    private Tile toTile(Entity entity) {
        WorldTile wt = WorldTile.get(entity);
        return Tile.newBuilder().withForegroundColor(colorCache.getUnchecked(wt.getColor())).withCharacter(wt.getGlyph(entity)).buildCharacterTile();
    }

    private void updateSideBar(SideBar sideBar, Game game) {
        Map<String, Component> componentsByName = sideBar.getComponentsByName();
        World world = game.getState().getWorld();
        Entity player = getPlayer(game);
        CharacterSheet playerSheet = CharacterSheet.fromEntity(player);

        String worldText = "Day " + world.getCalendar().getDayOfYear() + ", year " + world.getCalendar().getYear();
        worldText += "\n" + world.getCalendar().getTime() + "  " + world.getCalendar().getSeason().getDisplay();
        ((TextArea) componentsByName.get("world")).setText(worldText);

        // Gathering these attributes could be expensive.. should only
        // update when needed..
        Set<Attribute> stats = playerSheet.getStats().getAttributes();
        StringBuilder statsBuilder = new StringBuilder();

        int perRow = 1;
        for (var stat : stats) {
            statsBuilder.append(stat.getName()).append(":").append(stat.getValue());

            if (perRow == 2) {
                statsBuilder.append("\n");
                perRow = 1;
            } else {
                statsBuilder.append("  ");
                perRow++;
            }
        }

        ((TextArea) componentsByName.get("stats"))
                .setText(statsBuilder.toString());

        String status = playerSheet.getStatus().getEffects().stream()
                .collect(Collectors.joining(", "));
        ((TextOverride) componentsByName.get("status"))
                .setText("Age: " + playerSheet.getStatus().getAge() + "  "
                        + "HP: " + playerSheet.getStatus().getHitPoints() + "\n" + status);
        //
        // TODO: list NPCs nearby
        // TODO: log area is SLOW
        //            LogArea logArea = (LogArea) componentsByName.get("log");
        //            logArea.clear();
        //
        //            for (String recent : game.getState().getLog().getRecent(5)) {
        //                logArea.addHeader(recent, false);
        //            }
    }

    private void updateGameScreen(Layer gameScreen, Game game) {
        Location playerLocation = getPlayerLocation(game);
        int xHalf = gameScreen.getSize().getWidth() / 2;
        int yHalf = gameScreen.getSize().getHeight() / 2;
        // Note these are zero-based
        float[][] lightResistances = fovHelper.generateSimpleResistances(Location.create(playerLocation.getX() - xHalf, playerLocation.getY() - yHalf), Location.create(playerLocation.getX() + xHalf, playerLocation.getY() + yHalf));
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

                // TODO: simple caching needs performance testing
                Position uiScreenPosition = Position.create(x, y);
                Location loc = Location.create(getX, getY);
                Entity npc = GameUI.getNPC(game, loc);
                if (locationLightLevelPct > .25) {
                    if (npc == null) {
                        Entity terrainEnt = game.getState().getWorld().getTerrain(loc);
                        Maybe<Tile> existingTile = gameScreen.getTileAt(uiScreenPosition);
                        Tile bottomTile = existingTile.get();
                        boolean drawTile = true;
                        if (bottomTile != null) {
                            String existingTileHash = bottomTile.getCacheKey();
                            bottomTile = toTile(bottomTile, terrainEnt);
                            drawTile = !bottomTile.getCacheKey().equals(existingTileHash);
                        } else {
                            bottomTile = toTile(terrainEnt);
                        }
                        if (drawTile) {
                            gameScreen.draw(bottomTile, uiScreenPosition);
                        }
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
        Map<Location, Color> miniMapLocations = GameUI.createMiniMap(game.getState().getWorld(), getPlayerLocation(game));
        for (Location loc : miniMapLocations.keySet()) {
            Color color = miniMapLocations.get(loc);
            TileColor tileColor = colorCache.getUnchecked(color);
            Tile tile = BLANK_TILE.createCopy().withBackgroundColor(tileColor).withForegroundColor(ANSITileColor.BRIGHT_CYAN);
            Position offset = Position.create(loc.getX(), loc.getY());
            miniMap.draw(tile, offset);
        }
    }
}
