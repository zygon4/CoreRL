/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.util.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.zygon.rl.world.action.Action;

/**
 *
 * @author djc
 */
public class Dialog {

    private final String greeting; // top level information/greeting
    private final Optional<Action> action; // give/take item, etc.
    private final List<DialogChoice> choices;

    private Dialog(String headerText, Optional<Action> action,
            List<DialogChoice> choices) {
        this.greeting = Objects.requireNonNull(headerText);
        this.action = action;
        this.choices = choices != null
                ? Collections.unmodifiableList(choices)
                : Collections.emptyList();
    }

    public String getGreeting() {
        return greeting;
    }

    public Optional<Action> getAction() {
        return action;
    }

    public List<DialogChoice> getChoices() {
        return choices;
    }

    public boolean isTerminal() {
        return choices.isEmpty();
    }

    public static Dialog create(String text, Optional<Action> action,
            List<DialogChoice> options) {
        return new Dialog(text, action, options);
    }

    public static Dialog create(String text, Optional<Action> action) {
        return create(text, action, null);
    }

    public static Dialog create(String text) {
        return create(text, Optional.empty());
    }

    public final Dialog addChoices(List<DialogChoice> choices) {
        List<DialogChoice> allChoices = new ArrayList<>(getChoices());
        allChoices.addAll(choices);
        return new Dialog(greeting, action, choices);
    }
}
