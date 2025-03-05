/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.action;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.zygon.rl.game.DialogInputHandler;
import com.zygon.rl.game.GameConfiguration;
import com.zygon.rl.game.GameState;
import com.zygon.rl.game.quest.DialogueQuestContext;
import com.zygon.rl.game.quest.FetchQuestContext;
import com.zygon.rl.game.quest.QuestType;
import com.zygon.rl.util.dialog.Dialog;
import com.zygon.rl.util.dialog.DialogChoice;
import com.zygon.rl.util.dialog.DialogSession;
import com.zygon.rl.world.Location;
import com.zygon.rl.world.character.CharacterSheet;

/**
 *
 * @author djc
 */
public class DialogAction extends Action {

    private static final System.Logger LOGGER = System.getLogger(DialogAction.class.getCanonicalName());

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

    private static DialogChoice createDoneChoice(
            FetchQuestContext fetchQuestContext, String targetName,
            Location targetLocation) {
        return DialogChoice.create("This quest is complete",
                Optional.of(fetchQuestContext.present(targetName, targetLocation)));
    }

    @Override
    public GameState execute(GameState state) {
        CharacterSheet target = state.getWorld().get(this.location);
        Dialog dialog = target.getDialog();

        // Are there outstanding fetch quests for this NPC?
        Set<FetchQuestContext> fetchQuests = state.getQuestState()
                .getByType(QuestType.FETCH);

        if (fetchQuests != null) {
            for (var fetch : fetchQuests) {

                // fetch quest is done and we're about to speak to the person
                // who gave it to us. Let's remove the dialog choice..
//                if (fetch.isComplete(state) && fetch.isTarget(target.getName())) {
//                    dialog.removeChoice(choice);
//
//                    dialog.getChoices().stream()
//                            .filter(dc -> dc.)
//
//                }
                // add "job done" dialog choice, generic
                if (!fetch.isComplete(state) && fetch.isTarget(target.getName())) {
                    // If subtasks are successful, add a "job is done" choice with action that removes the item
                    // if the subtasks have failed, "sorry",
                    dialog = dialog.addChoices(List.of(createDoneChoice(fetch, target.getName(), location)));
                }
            }
        }

        DialogInputHandler dialogHandler = DialogInputHandler.create(
                this.gameConfiguration, DialogSession.play(dialog));

        Set<DialogueQuestContext> dialogueQuests = state.getQuestState()
                .getByType(QuestType.DIALOGUE);

        if (dialogueQuests != null) {
            for (var quest : dialogueQuests) {
                if (quest.spokeWith(target.getName())) {
                    state.getLog().add("Quest complete: " + quest);

                    //                state = state.copy()
                    //                        .setNotification(Notification.create("Quest complete: " + quest, true))
                    //                        .build();
                }
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
