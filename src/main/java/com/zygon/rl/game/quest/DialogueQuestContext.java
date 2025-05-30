/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.quest;

import com.zygon.rl.game.GameState;
import com.zygon.rl.util.quest.QuestInfo;

/**
 *
 * @author djc
 */
public class DialogueQuestContext implements QuestInfo.QuestContext<GameState> {

    private final String targetName;
    private boolean complete = false;

    public DialogueQuestContext(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public boolean isComplete(GameState gs) {
        return complete;
    }

    @Override
    public boolean isSuccess(GameState gs) {
        return complete; // same as complete, check if target is alive?
    }

    // Hate mutable data but let's go with this for now..
    /**
     * Checks if the target matches this context and sets the complete flag if
     * so. Returns true if the context is complete.
     *
     * @param targetName
     * @return
     */
    public boolean spokeWith(String targetName) {
        if (this.targetName.equals(targetName) && !this.complete) {
            this.complete = true;
        }
        return this.complete;
    }

    @Override
    public String toString() {
        return "Speak with " + this.targetName;
    }
}
