/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.util.dialog;

import java.util.Objects;
import java.util.Optional;

import com.zygon.rl.world.action.Action;

/**
 *
 * @author djc
 */
public class DialogChoice {

    private final String message; // TODO: subtext descriptor ie. "(attack $foo)"
    private final Optional<Action> action; // attack, give item, etc.
    private final Optional<Dialog> transition; // optional move dialog state

    private DialogChoice(String message, Optional<Action> action,
            Optional<Dialog> transition) {
        this.message = Objects.requireNonNull(message);
        this.action = action;
        this.transition = transition;
    }

    public String getMessage() {
        return message;
    }

    public Optional<Action> getAction() {
        return action;
    }

    public Optional<Dialog> getTransition() {
        return transition;
    }

    public static DialogChoice create(String message, Optional<Action> action,
            Optional<Dialog> transition) {
        return new DialogChoice(message, action, transition);
    }

    public static DialogChoice create(String message, Optional<Action> action) {
        return create(message, action, Optional.empty());
    }

    public static DialogChoice create(String message) {
        return create(message, Optional.empty());
    }
}
