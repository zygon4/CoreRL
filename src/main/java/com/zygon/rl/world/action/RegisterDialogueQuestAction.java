/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import java.util.List;

import com.zygon.rl.game.GameState;
import com.zygon.rl.util.quest.QuestInfo;
import com.zygon.rl.world.character.CharacterSheet;
import com.zygon.rl.world.quest.DialogueQuestContext;
import com.zygon.rl.world.quest.QuestType;

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

    @Override
    public GameState execute(GameState state) {

        // TODO; this is for testing..
        QuestInfo subQuest = new QuestInfo("Locate " + this.targetName,
                this.targetName + " was last seen by the swamp.",
                new QuestInfo.QuestContext() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public boolean isSuccess() {
                return true;
            }
        });

        DialogueQuestContext dialogue = new DialogueQuestContext(this.targetName);
        QuestInfo quest = new QuestInfo("Speak to " + this.targetName,
                "Seek out and speak with " + this.targetName + " to discuss your vampirism.",
                dialogue, List.of(subQuest));

        CharacterSheet sheet = state.getWorld().getPlayer().add(quest);
        Action setCharacterAction
                = new SetCharacterAction(state.getWorld().getPlayerLocation(), sheet);

        GameState newState = setCharacterAction.execute(state);
        newState.getQuestState().register(QuestType.DIALOGUE, dialogue);

        return newState;
    }
}
