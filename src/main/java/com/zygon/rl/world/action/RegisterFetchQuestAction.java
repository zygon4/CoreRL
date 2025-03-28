/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import java.util.List;

import com.zygon.rl.data.Element;
import com.zygon.rl.data.context.Data;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.quest.FetchQuestContext;
import com.zygon.rl.game.quest.GatherQuestContext;
import com.zygon.rl.game.quest.QuestType;
import com.zygon.rl.util.quest.QuestInfo;
import com.zygon.rl.world.character.CharacterSheet;

/**
 * Only for the player..
 * <br>
 * Two sets of dialog:
 * <br>
 * 1) the quest overview ("get this for her") 2) the quest dialog choice(s) when
 * speaking to that person ("the job is done")
 *
 * @author djc
 */
public class RegisterFetchQuestAction extends Action {

    private final String targetName;
    private final String itemId;

    public RegisterFetchQuestAction(String targetName, String itemId) {
        this.targetName = targetName;
        this.itemId = itemId;
    }

    @Override
    public boolean canExecute(GameState state) {
        return Boolean.TRUE;
    }

    @Override
    public GameState execute(GameState state) {

        Element item = Data.get(itemId);
        String name = item.getName();

        GatherQuestContext gatherQuestContext = new GatherQuestContext(this.itemId);
        QuestInfo locateItem = new QuestInfo("Locate [" + name + "] for "
                + this.targetName, this.targetName + " has\nrequested [" + name + "]",
                gatherQuestContext);

        FetchQuestContext fetchCtx = new FetchQuestContext(gatherQuestContext, this.itemId, this.targetName);
        QuestInfo fetchQuest = new QuestInfo(this.targetName + " has requested [" + name + "]",
                "Seek out\n[" + name + "] and provided it to " + this.targetName,
                fetchCtx, List.of(locateItem));

        CharacterSheet sheet = state.getWorld().getPlayer().add(fetchQuest);
        Action setCharacterAction
                = new SetCharacterAction(sheet, state.getWorld().getPlayerLocation());

        // TODO: notifications
        GameState newState = setCharacterAction.execute(state);
        newState.getQuestState().register(QuestType.FETCH, fetchCtx);

        return newState;
    }
}
