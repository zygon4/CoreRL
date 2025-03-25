package com.zygon.rl.game.ui.core;

import com.zygon.rl.game.Game;

import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.ColorThemes;
import org.hexworks.zircon.api.SwingApplications;
import org.hexworks.zircon.api.application.AppConfig;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.grid.TileGrid;

/**
 *
 * @author zygon
 */
public class GameUI {

    private final Game game;

    public GameUI(Game game) {
        this.game = game;
    }

    public void start() {
        //LibgdxApplications
        TileGrid tileGrid = SwingApplications.startTileGrid(
                AppConfig.newBuilder()
                        .withTitle(game.getConfiguration().getGameName() + "      " + POWERED_BY)
                        .withSize(Size.create(120, 70))
                        //                        .withDebugMode(true)
                        //                        .withDebugConfig(DebugConfig.newBuilder().withRelaxBoundsCheck(true).build())
                        .withDefaultTileset(CP437TilesetResources.rexPaint16x16())
                        .build());

        TitleView titleView = new TitleView(tileGrid, ColorThemes.afterDark(), game);

        titleView.dock();
    }

    private static final String POWERED_BY = "[powered by https://github.com/zygon4/CoreRL]";
}
