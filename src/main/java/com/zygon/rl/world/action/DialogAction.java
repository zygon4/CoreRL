/*
 * Copyright Liminal Data Systems 2024
 */
package com.zygon.rl.world.action;

import com.zygon.rl.game.DialogInputHandler;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.util.dialog.DialogSession;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author djc
 */
public class DialogAction extends Action {

    private final GameConfiguration gameConfiguration;
    private final Location location;

    public DialogAction(GameConfiguration gameConfiguration, Location location) {
        this.gameConfiguration = gameConfiguration;
        this.location = location;
    }

    @Override
    public boolean canExecute(GameState state) {
        CharacterSheet sheet = state.getWorld().get(location);
        if (sheet == null) {
            return false;
        }

        return sheet.getDialog() != null;
    }

    @Override
    public GameState execute(GameState state) {
        CharacterSheet sheet = state.getWorld().get(this.location);
        DialogInputHandler dialogHandler = DialogInputHandler.create(
                this.gameConfiguration, DialogSession.play(sheet.getDialog()));

        return state.copy()
                .addInputContext(
                        GameState.InputContext.builder()
                                .setName("TEXT")
                                .setHandler(dialogHandler)
                                .setPrompt(GameState.InputContextPrompt.DIALOG)
                                .build()).build();
    }
}
