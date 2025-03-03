/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.util.dialog;

import java.util.ArrayList;
import java.util.Collection;
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
    private final List<DialogChoice> choices = new ArrayList<>();

    private Dialog(String headerText, Optional<Action> action,
            Collection<DialogChoice> choices) {
        this.greeting = Objects.requireNonNull(headerText);
        this.action = action;
        if (choices != null) {
            this.choices.addAll(choices);
        }
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

    // MUTATES
    public Dialog addChoices(List<DialogChoice> choices) {
        this.choices.addAll(choices);
        return this;
    }
}
