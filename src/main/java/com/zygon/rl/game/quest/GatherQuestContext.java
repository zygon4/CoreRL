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
public final class GatherQuestContext implements QuestInfo.QuestContext<GameState> {

    private final String itemId;
    private Boolean complete = null;

    public GatherQuestContext(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean isComplete(GameState gs) {
        return (complete != null && complete)
                || gs.getWorld().getPlayer().getInventory().getItem(this.itemId) != null;
    }

    @Override
    public boolean isSuccess(GameState gs) {
        // failure if the item is broken??
        return isComplete(gs);
    }

    void setComplete() {
        this.complete = Boolean.TRUE;
    }
}
