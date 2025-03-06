/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.zygon.rl.game.GameState.InputContext;
import com.zygon.rl.util.dialog.DialogSession;

import org.hexworks.zircon.api.uievent.KeyCode;

public final class DialogInputHandler extends BaseInputHandler {

    private final DialogSession dialogSession;
    private final Map<Input, Integer> choiceIndexByKeyCode;

    private DialogInputHandler(GameConfiguration gameConfiguration,
            DialogSession dialogSession, Map<Input, Integer> itemsByKeyCode) {
        super(gameConfiguration, getInputs(itemsByKeyCode));
        this.dialogSession = dialogSession;
        this.choiceIndexByKeyCode = itemsByKeyCode;
    }

    public static final DialogInputHandler create(
            GameConfiguration gameConfiguration, DialogSession dialogSession) {

        // Storing Input->Integer as the Integer is the key into the choices
        Map<Input, Integer> inputs = createAlphaInputs(
                dialogSession.getDialogChoices().keySet().stream()
                        .collect(Collectors.toList()));
        return new DialogInputHandler(gameConfiguration, dialogSession, inputs);
    }

    private static Set<Input> getInputs(Map<Input, Integer> choiceIndexByKeyCode) {
        Set<Input> inputs = new LinkedHashSet<>(choiceIndexByKeyCode.keySet());
        inputs.add(Input.valueOf(KeyCode.ESCAPE.getCode()));
        return inputs;
    }

    @Override
    public GameState handleInvalidInput(GameState state) {
        // Don't pop the state
        return state;
    }

    @Override
    public GameState apply(final GameState state, Input input) {

        final AtomicReference<GameState> newState = new AtomicReference<>(state);

        switch (convert(input)) {
            case ESCAPE -> {
                newState.set(newState.get().copy()
                        .removeInputContext()
                        .build());
            }
            default -> {
                if (choiceIndexByKeyCode.containsKey(input)) {
                    Integer dialogIndex = choiceIndexByKeyCode.get(input);
                    DialogSession session = dialogSession.pick(state, dialogIndex);

                    // Done..
                    if (session == null) {
                        newState.set(newState.get().copy()
                                .removeInputContext()
                                .build());
                    } else {
                        session.getAction().ifPresent(a -> {
                            if (a.canExecute(newState.get())) {
                                newState.set(a.execute(newState.get()));
                            }
                        });

                        if (session.getResultingState() != null) {
                            newState.set(session.getResultingState());
                        }

                        newState.set(newState.get().copy()
                                .removeInputContext()
                                .addInputContext(InputContext.builder()
                                        .setName("DIALOG")
                                        .setPrompt(GameState.InputContextPrompt.DIALOG)
                                        .setHandler(DialogInputHandler.create(getGameConfiguration(), session))
                                        .build())
                                .build());

                    }
                }
            }
        }

        return newState.get();
    }

    @Override
    public String getDisplayText(Input input) {
        if (convert(input) == KeyCode.ESCAPE) {
            return "ESC";
        }

        Integer dialogIndex = choiceIndexByKeyCode.get(input);
        return this.dialogSession.getDialogChoices().get(dialogIndex);
    }

    private DialogSession getDialogSession() {
        return dialogSession;
    }

    public static Function<GameState, List<String>> getTextFn() {
        return gameState -> {
            InputContext ic = gameState.getInputContext().peek();
            if (ic.getPrompt() == GameState.InputContextPrompt.DIALOG) {
                LayerInputHandler handler = ic.getHandler();

                // Casting is usually a hack.. this is no exception..
                DialogInputHandler dialogInputHandler = (DialogInputHandler) handler;
                final DialogSession dialogSession = dialogInputHandler.getDialogSession();

                List<String> text = new ArrayList<>();
                text.add(dialogSession.getGreeting());
                text.add("");
                text.add("");

                // printing the inputs is definitely something to be commonized.
                dialogInputHandler.getInputs().stream()
                        .filter(input -> convert(input) != KeyCode.ESCAPE) // filter out implied escape character
                        .map(input -> input + ") " + handler.getDisplayText(input))
                        .forEach(text::add);
                return text;
            }
            return null;
        };
    }
}
