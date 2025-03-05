/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game.quest;

import com.zygon.rl.game.GameState;
import com.zygon.rl.util.quest.QuestInfo;
import com.zygon.rl.world.Item;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.action.Action;
import com.zygon.rl.world.action.SetCharacterAction;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author djc
 */
public class FetchQuestContext implements QuestInfo.QuestContext<GameState> {

    // This links both contexts.. I don't love it, but it works
    private final GatherQuestContext gatherQuest;
    private final String itemId;
    private final String targetName;
    private boolean complete = false;

    public FetchQuestContext(GatherQuestContext gatherQuest, String itemId,
            String targetName) {
        this.gatherQuest = gatherQuest;
        this.itemId = itemId;
        this.targetName = targetName;
    }

    @Override
    public boolean isComplete(GameState gs) {
        return complete;
    }

    @Override
    public boolean isSuccess(GameState gs) {
        return isComplete(gs);
    }

    public boolean isTarget(String targetName) {
        return this.targetName.equals(targetName);
    }

    /**
     *
     * @param targetName
     * @param targetLocation
     * @return
     */
    public Action present(String targetName, Location targetLocation) {
        return new Action() {
            @Override
            public boolean canExecute(GameState state) {
                CharacterSheet player = state.getWorld().getPlayer();
                boolean hasItem = player.getInventory().getItem(itemId) != null;

                return hasItem && FetchQuestContext.this.targetName.equals(targetName)
                        && !FetchQuestContext.this.isComplete(state);
            }

            @Override
            public GameState execute(GameState state) {
                CharacterSheet player = state.getWorld().getPlayer();
                Item item = player.getInventory().getItem(itemId);
                player = player.remove(item);

                SetCharacterAction removeItem = new SetCharacterAction(
                        state.getWorld().getPlayerLocation(), player);
                if (removeItem.canExecute(state)) {
                    state = removeItem.execute(state);
                }

                CharacterSheet target = state.getWorld().get(targetLocation); // Do the exchange
                SetCharacterAction addItem = new SetCharacterAction(
                        targetLocation, target);
                if (addItem.canExecute(state)) {
                    state = addItem.execute(state);
                }

                // Hate mutable data but let's go with this for now..
                FetchQuestContext.this.gatherQuest.setComplete();
                FetchQuestContext.this.complete = true;

                return state;
            }
        };
    }

    @Override
    public String toString() {
        return "Retrieve " + this.itemId + " for " + this.targetName;
    }
}
