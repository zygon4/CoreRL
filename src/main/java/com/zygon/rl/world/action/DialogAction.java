/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import java.util.Set;

import com.zygon.rl.game.DialogInputHandler;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.Notification;
import com.zygon.rl.util.dialog.DialogSession;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.quest.DialogueQuestContext;
import com.zygon.rl.world.quest.QuestType;

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
        CharacterSheet target = state.getWorld().get(location);
        return target != null && target.getDialog() != null;
    }

    @Override
    public GameState execute(GameState state) {
        CharacterSheet target = state.getWorld().get(this.location);
        DialogInputHandler dialogHandler = DialogInputHandler.create(
                this.gameConfiguration, DialogSession.play(target.getDialog()));

        Set<DialogueQuestContext> dialogueQuests = state.getQuestState()
                .getByType(QuestType.DIALOGUE);

        for (var quest : dialogueQuests) {
            if (quest.spokeWith(target.getName())) {
                state = state.copy()
                        .setNotification(Notification.create("Quest complete: " + quest, true))
                        .build();
            }
        }

        return state.copy()
                .addInputContext(
                        GameState.InputContext.builder()
                                .setName("TEXT")
                                .setHandler(dialogHandler)
                                .setPrompt(GameState.InputContextPrompt.DIALOG)
                                .build()).build();
    }
}
