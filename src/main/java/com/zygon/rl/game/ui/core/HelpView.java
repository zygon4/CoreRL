package com.zygon.rl.game.ui.core;

import com.zygon.rl.game.Game;

import org.hexworks.zircon.api.CP437TilesetResources;
import org.hexworks.zircon.api.Components;
import org.hexworks.zircon.api.component.Button;
import org.hexworks.zircon.api.component.ComponentAlignment;
import org.hexworks.zircon.api.component.Panel;
import org.hexworks.zircon.api.data.Size;
import org.hexworks.zircon.api.graphics.BoxType;
import org.hexworks.zircon.api.uievent.MouseEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;
import org.hexworks.zircon.api.view.base.BaseView;

// Game menu/help view, not associated with the game's input handlers
// This is similar to the TitleView *for now* but it should evolve
final class HelpView extends BaseView {

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
                .withDecorations(org.hexworks.zircon.api.ComponentDecorations.box(
                        BoxType.DOUBLE, game.getConfiguration().getGameName()))
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

        // this is for develop only
        Button controlsButton = Components.button()
                .withText("CONTROLS")
                .withAlignmentAround(continueButton, ComponentAlignment.BOTTOM_CENTER)
                .withTileset(CP437TilesetResources.rexPaint16x16())
                .build();

        controlsButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
            Panel modalPanel = Components.panel()
                    // Increase the height if you add more lines below
                    .withSize(Size.create(80, 16))
                    .withColorTheme(gameView.getTheme())
                    .withAlignmentWithin(titleMenuPanel, ComponentAlignment.CENTER)
                    .withDecorations(org.hexworks.zircon.api.ComponentDecorations.box(BoxType.SINGLE))
                    .build();
            // TODO: these should be pulled from the default input handler as
            // self-organizing help content
            modalPanel.addComponent(Components.textBox(70)
                    .addHeader("1,2,3,6,7,8,9   - movement", false)
                    .addHeader("5               - wait a turn", false)
                    .addHeader("a               - view abilities", false)
                    .addHeader("c               - close door", false)
                    .addHeader("g               - get item", false)
                    .addHeader("d               - drop item", false)
                    .addHeader("i               - view inventory", false)
                    .addHeader("e               - examine surroundings", false)
                    .addHeader("t               - talk to someone adjacent (use numpad)", false)
                    .addHeader("q               - view quests", false)
                    .addHeader("p               - view player", false)
                    .addHeader("x               - scan around using 1-9, ENTER or '5' to look", false)
                    .addHeader("F1,F2,F3        - experimental spells. F3 aims using 1-9 then ENTER", false)
                    .addHeader("esc             - game menu", false)
                    .build());
            getScreen().openModal(new Dialog(getScreen().getSize(), modalPanel));
            return UIEventResponse.processed();
        });

        // TODO: store/load game
        Button quitButton = Components.button()
                .withText("QUIT")
                .withAlignmentAround(controlsButton, ComponentAlignment.BOTTOM_CENTER)
                .withTileset(CP437TilesetResources.rexPaint16x16())
                .build();

        quitButton.handleMouseEvents(MouseEventType.MOUSE_CLICKED, (p1, p2) -> {
            System.exit(0);
            return UIEventResponse.processed();
        });

        titleMenuPanel.addComponent(continueButton);
        titleMenuPanel.addComponent(controlsButton);
        titleMenuPanel.addComponent(quitButton);
        getScreen().addComponent(titleMenuPanel);
    }

}
