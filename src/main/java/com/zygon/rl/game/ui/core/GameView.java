package com.zygon.rl.game.ui.core;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.awt.Color;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.game.AbilityInputHandler;
import com.zygon.rl.game.DialogInputHandler;
import com.zygon.rl.game.Game;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.Input;
import com.zygon.rl.game.InventoryInputHandler;
import com.zygon.rl.game.ItemInputHandler;
import com.zygon.rl.game.ui.render.AbilityRenderer;
import com.zygon.rl.game.ui.render.GameComponentRenderer;
import com.zygon.rl.game.ui.render.InventoryRenderer;
import com.zygon.rl.game.ui.render.ItemRenderer;
import com.zygon.rl.game.ui.render.MapRenderer;
import com.zygon.rl.game.ui.render.OuterWorldRenderer;
import com.zygon.rl.game.ui.render.PlayerRenderer;
import com.zygon.rl.game.ui.render.QuestRenderer;
import com.zygon.rl.game.ui.render.RenderUtil;
import com.zygon.rl.game.ui.render.TextRenderer;
import com.zygon.rl.util.Audio;
import com.zygon.rl.world.Attribute;
import com.zygon.rl.world.World;
import com.zygon.rl.world.character.CharacterSheet;

import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.Functions;
import org.hexworks.zircon.api.behavior.TextOverride;
import org.hexworks.zircon.api.color.ANSITileColor;
import org.hexworks.zircon.api.color.TileColor;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.Component;
import org.hexworks.zircon.api.component.ComponentAlignment;
import org.hexworks.zircon.api.component.Header;
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

/**
 *
 * @author zygon
 */
final class GameView extends BaseView {

    private static final System.Logger logger = System.getLogger(GameView.class.getCanonicalName());

    private static final int SIDEBAR_SCREEN_WIDTH = 24;
    // TODO: support for different kinds of extra-sensory vision
    private static final Tile BLACK_TILE = Tile.newBuilder()
            .withBackgroundColor(ANSITileColor.BLACK)
            .withForegroundColor(ANSITileColor.BLACK)
            .buildCharacterTile();
    private final LoadingCache<Color, TileColor> colorCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build(new CacheLoader<Color, TileColor>() {
                @Override
                public TileColor load(Color key) {
                    return RenderUtil.convert(key);
                }
            });

    private final Map<GameState.InputContextPrompt, GameComponentRenderer> componentRenderersByPrompt = new HashMap<>();

    private final TileGrid tileGrid;
    private Game game;
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
                    audio.playLoop();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
                    logger.log(System.Logger.Level.ERROR, "Unable to play music file: " + musicFile, ex);
                }
            }

            VBox gameScreen = Components.vbox()
                    .withPreferredSize(tileGrid.getSize().getWidth() - SIDEBAR_SCREEN_WIDTH, tileGrid.getSize().getHeight() - 3)
                    .withDecorations(org.hexworks.zircon.api.ComponentDecorations.box(BoxType.DOUBLE))
                    .withAlignmentWithin(tileGrid, ComponentAlignment.TOP_LEFT)
                    .build();
            getScreen().addComponent(gameScreen);

            final Size gameScreenSize = gameScreen.getSize();

            sideBar = createSideBar(Position.create(gameScreen.getWidth(), 0), game);
            getScreen().addFragment(sideBar);

            miniMapLayer = Layer.newBuilder()
                    .withSize(SIDEBAR_SCREEN_WIDTH, SIDEBAR_SCREEN_WIDTH)
                    .withOffset(gameScreenSize.getWidth() + 1, sideBar.getRoot().getHeight() + 1)
                    .build();
            getScreen().addLayer(miniMapLayer);

            RenderUtil renderUtil = new RenderUtil();

            Layer gameScreenLayer = Layer.newBuilder()
                    .withSize(gameScreenSize)
                    .build();
            getScreen().addLayer(gameScreenLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.PRIMARY,
                    new OuterWorldRenderer(gameScreenLayer, game, renderUtil));

            final Size overlayScreenSize = gameScreenSize.minus(Size.create(20, 20));
            final Position overlayScreenPos = gameScreen.getPosition().plus(Position.create(9, 11));

            Layer abilityLayer = Layer.newBuilder()
                    .withSize(overlayScreenSize.minus(Size.create(0, 37)))
                    .withOffset(overlayScreenPos.plus(Position.create(0, 40)))
                    .build();
            getScreen().addLayer(abilityLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.ABILITIES,
                    new AbilityRenderer(abilityLayer, renderUtil, AbilityInputHandler.getInputsFn()));

            Layer inventoryLayer = Layer.newBuilder()
                    .withSize(overlayScreenSize)
                    .withOffset(overlayScreenPos)
                    .build();
            getScreen().addLayer(inventoryLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.INVENTORY,
                    new InventoryRenderer(inventoryLayer, renderUtil, InventoryInputHandler.getInputsFn()));

            Layer itemLayer = Layer.newBuilder()
                    .withSize(overlayScreenSize)
                    .withOffset(overlayScreenPos)
                    .build();
            getScreen().addLayer(itemLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.ITEM,
                    new ItemRenderer(game.getConfiguration(), itemLayer, renderUtil, ItemInputHandler.getInputsFn()));

            Layer mapLayer = Layer.newBuilder()
                    .withSize(overlayScreenSize)
                    .withOffset(overlayScreenPos)
                    .build();
            getScreen().addLayer(mapLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.MAP,
                    new MapRenderer(mapLayer, renderUtil));

            Layer notificationLayer = Layer.newBuilder()
                    .withSize(overlayScreenSize)
                    .withOffset(overlayScreenPos)
                    .build();
            getScreen().addLayer(notificationLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.NOTIFICATION,
                    new TextRenderer(notificationLayer, renderUtil,
                            (gs) -> gs.getNotification() != null ? List.of(gs.getNotification().note()) : null));

            Layer playerLayer = Layer.newBuilder()
                    .withSize(overlayScreenSize)
                    .withOffset(overlayScreenPos)
                    .build();
            getScreen().addLayer(playerLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.PLAYER,
                    new PlayerRenderer(playerLayer, renderUtil));

            Layer dialogLayer = Layer.newBuilder()
                    .withSize(overlayScreenSize)
                    .withOffset(overlayScreenPos)
                    .build();
            getScreen().addLayer(dialogLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.DIALOG,
                    new TextRenderer(dialogLayer, renderUtil, DialogInputHandler.getTextFn()));

            Layer questLayer = Layer.newBuilder()
                    .withSize(overlayScreenSize)
                    .withOffset(overlayScreenPos)
                    .build();
            getScreen().addLayer(questLayer);
            componentRenderersByPrompt.put(GameState.InputContextPrompt.QUESTS,
                    new QuestRenderer(questLayer, renderUtil));

            updateSideBar(sideBar, game);
            MapRenderer.updateMiniMap(miniMapLayer, game.getState(), renderUtil);
            updateGameScreen(game);

            // At bottom of screen
            Header promptHeader = Components.header()
                    .withSize(50, 1)
                    .withPosition(1, gameScreen.getHeight() + 1)
                    .build();
            getScreen().addComponent(promptHeader);
            promptHeader.setHidden(true);

            tileGrid.processKeyboardEvents(KeyboardEventType.KEY_PRESSED,
                    Functions.fromBiConsumer((event, phase) -> {
                        Input input = Input.valueOf(event.getCode().getCode());

                        long turnStart = System.nanoTime();
                        game = game.turn(input);
                        logger.log(System.Logger.Level.TRACE, "turn (ms) "
                                + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - turnStart));

                        long updateGameScreen = System.nanoTime();
                        updateGameScreen(game);
                        logger.log(System.Logger.Level.TRACE, "game screen (ms) "
                                + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - updateGameScreen));

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
                                    switch (inputCtx.getName()) {
                                        case "GAME_MENU" ->
                                            // This uses UI engine components to register model open/close
                                            replaceWith(new HelpView(this, game));
                                    }
                                    break;
                            }
                        }
                        updateSideBar(sideBar, game);
                        // I didn't intend to leave this "delay" in here, but it's not a terrible idea..
                        if (game.getState().getTurnCount() % 10 == 0) {
                            MapRenderer.updateMiniMap(miniMapLayer, game.getState(), renderUtil);
                        }
                    }));
            initialized = true;
        }
    }

    private SideBar createSideBar(Position position, Game game) {
        // TODO: creation method
        Map<String, Component> componentsByName = new LinkedHashMap<>();
        componentsByName.put("world", Components.textArea()
                .withSize(SIDEBAR_SCREEN_WIDTH - 2, 4).build());
        componentsByName.put("stats", Components.textArea()
                .withSize(SIDEBAR_SCREEN_WIDTH - 2, 4).build());
        componentsByName.put("status", Components.label()
                .withSize(SIDEBAR_SCREEN_WIDTH - 2, 5).build());
        // TODO: full screen log area (view?) to see/search all
        componentsByName.put("log", Components.textArea()
                .withSize(SIDEBAR_SCREEN_WIDTH - 2, 30)
                .build());

        String playerName = getPlayer(game).getName();
        return new SideBar(componentsByName,
                Size.create(SIDEBAR_SCREEN_WIDTH, tileGrid.getSize().getHeight() - SIDEBAR_SCREEN_WIDTH),
                position,
                playerName);
    }

    private CharacterSheet getPlayer(Game game) {
        return game.getState().getWorld().getPlayer();
    }

    private void updateSideBar(SideBar sideBar, Game game) {
        Map<String, Component> componentsByName = sideBar.getComponentsByName();
        World world = game.getState().getWorld();
        CharacterSheet playerSheet = getPlayer(game);

        String worldText = "Day " + world.getCalendar().getDayOfYear() + ", year " + world.getCalendar().getYear();
        worldText += "\n" + world.getCalendar().getTime() + "  " + world.getCalendar().getSeason().getDisplay();
        worldText += "\n" + world.getWeather().getDisplayName();
        ((TextArea) componentsByName.get("world")).setText(worldText);

        // Gathering these attributes could be expensive.. should only
        // update when needed..
        Set<Attribute> stats = playerSheet.getModifiedStats().getAttributes();
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

        // TODO: Needs better layout
        String status = playerSheet.getStatus().getEffects().entrySet().stream()
                .map(entry -> Data.get(entry.getKey()).getName())
                .collect(Collectors.joining("\r\n"));

        ((TextOverride) componentsByName.get("status"))
                .setText("Age: " + playerSheet.getStatus().getAge() + "  "
                        + "HP: " + playerSheet.getStatus().getHitPoints()
                        + " Spd: " + playerSheet.getSpeed() + "\r\n"
                        + status);
        //
        // TODO: list NPCs nearby
        // TODO: log area is SLOW so use text area
        TextArea logArea = (TextArea) componentsByName.get("log");

        String collect = game.getState().getLog()
                .getRecent(10, logArea.getWidth()).stream()
                .collect(Collectors.joining("\n"));

        logArea.setText(collect);
    }

    // Added a loop, keep an eye out for performance issues
    private void updateGameScreen(Game game) {

        componentRenderersByPrompt.forEach((p, r) -> {
            // This is a HACK and shows that the *direction* handlers need better
            if (p != GameState.InputContextPrompt.DIRECTION) {
                r.clear();
            }
        });

        for (GameState.InputContext inputCtx : game.getState().getInputContext()) {
            logger.log(Level.DEBUG, "Rendering for input context {0}", inputCtx.getName());

            GameComponentRenderer gameComponentRenderer
                    = componentRenderersByPrompt.get(inputCtx.getPrompt());
            if (gameComponentRenderer != null) {
                gameComponentRenderer.render(game.getState());
            }
        }
    }
}
