/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import com.zygon.rl.game.GameState;
import com.zygon.rl.game.quest.DialogueQuestContext;
import com.zygon.rl.game.quest.QuestType;
import com.zygon.rl.util.quest.QuestInfo;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author djc
 */
public class RegisterDialogueQuestAction extends Action {

    private final String targetName;

    public RegisterDialogueQuestAction(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public boolean canExecute(GameState state) {
        // check if they're alive..
        return Boolean.TRUE;
    }

    // TODO: notification
    @Override
    public GameState execute(GameState state) {

        DialogueQuestContext dialogue = new DialogueQuestContext(this.targetName);
        QuestInfo quest = new QuestInfo("Speak to " + this.targetName,
                "Seek out and speak with " + this.targetName, dialogue);

        CharacterSheet sheet = state.getWorld().getPlayer().add(quest);
        Action setCharacterAction
                = new SetCharacterAction(state.getWorld().getPlayerLocation(), sheet);
        GameState newState = setCharacterAction.execute(state);

        newState.getQuestState().register(QuestType.DIALOGUE, dialogue);
        return newState;
    }
}
