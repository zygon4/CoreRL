package com.zygon.rl.game.ui.core;

import com.zygon.rl.game.Game;

import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.ColorTheme;
import org.hexworks.zircon.api.component.ComponentAlignment;
import org.hexworks.zircon.api.component.Header;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.grid.TileGrid;
import org.hexworks.zircon.api.uievent.MouseEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;
import org.hexworks.zircon.api.view.base.BaseView;

final class TitleView extends BaseView {

    private final TileGrid tileGrid;
    private final Game game;
    private final GameView gameView;
    private final String headerText;

    public TitleView(TileGrid tileGrid, ColorTheme colorTheme, Game game,
            String headerText) {
        super(tileGrid, colorTheme);
        this.tileGrid = tileGrid;
        this.game = game;
        this.gameView = new GameView(this.tileGrid, colorTheme, this.game);
        this.headerText = headerText;
    }

    public TitleView(TileGrid tileGrid, ColorTheme colorTheme, Game game) {
        this(tileGrid, colorTheme, game, null);
    }

    @Override
    public void onDock() {
        super.onDock();

        Panel titleMenuPanel = Components.panel()
                .withSize(tileGrid.getSize())
                .withDecorations(
                        org.hexworks.zircon.api.ComponentDecorations.box(
                                BoxType.DOUBLE, game.getConfiguration().getGameName()))
                .build();

        String headerStr = headerText == null ? "" : headerText;
        Header header = Components.header()
                .withText(headerStr)
                .withAlignmentWithin(titleMenuPanel, ComponentAlignment.CENTER)
                .build();
        header.setHidden(headerText == null);

        Button startButton = Components.button()
                .withText("NEW GAME")
                .withAlignmentAround(header, ComponentAlignment.BOTTOM_CENTER)
                .withTileset(CP437TilesetResources.rexPaint20x20())
                .build();

        startButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
            replaceWith(gameView);
            return UIEventResponse.processed();
        });

        // TODO: store/load game
        Button quitButton = Components.button()
                .withText("QUIT")
                .withAlignmentAround(startButton, ComponentAlignment.BOTTOM_CENTER)
                .withTileset(CP437TilesetResources.rexPaint20x20())
                .build();

        quitButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
            System.exit(0);
            return UIEventResponse.processed();
        });

        titleMenuPanel.addComponent(header);
        titleMenuPanel.addComponent(startButton);
        titleMenuPanel.addComponent(quitButton);
        getScreen().addComponent(titleMenuPanel);
    }

}
