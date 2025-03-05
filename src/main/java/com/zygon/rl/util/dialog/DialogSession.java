/*
 * Copyright Liminal Data Systems 2024
 */
package com.zygon.rl.util.dialog;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.zygon.rl.game.GameState;
import com.zygon.rl.world.action.Action;

/**
 * This is the lens on the Dialog state machine.
 *
 * @author djc
 */
public final class DialogSession {

    private final GameState state;
    private final Dialog dialog;

    private DialogSession(GameState state, Dialog dialog) {
        this.state = state;
        this.dialog = dialog;
    }

    public static DialogSession play(Dialog dialog) {
        return new DialogSession(null, dialog);
    }

    private static DialogSession play(GameState state, Dialog dialog) {
        return new DialogSession(state, dialog);
    }

    /**
     * Returns a {@link GameState} if the previous {@code pick} call had side
     * effects.
     *
     * @return
     */
    public GameState getResultingState() {
        return state;
    }

    public Optional<Action> getAction() {
        return this.dialog.getAction();
    }

    public String getGreeting() {
        return this.dialog.getGreeting();
    }

    public Map<Integer, String> getDialogChoices() {

        Map<Integer, String> choices = new LinkedHashMap<>();

        int index = 1;
        for (DialogChoice choice : this.dialog.getChoices()) {
            choices.put(index++, choice.getMessage());
        }

        return choices;
    }

    public DialogSession pick(GameState state, int choice) {
        int choiceIndex = choice - 1;
        DialogChoice dialogChoice = this.dialog.getChoices().get(choiceIndex);

        if (dialogChoice.getAction().isPresent()) {

            // can't execute, e.g. missing item
            Action action = dialogChoice.getAction().get();
            if (!action.canExecute(state)) {
                // TODO: log
                return this;
            }

            // Assumes side effect
            state = action.execute(state);
        }

        if (dialogChoice.getTransition().isPresent()) {
            Dialog transition = dialogChoice.getTransition().get();
            return play(state, transition);
        }

        // done..
        return null;
    }
}
