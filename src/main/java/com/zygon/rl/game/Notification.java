/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game;

/**
 *
 * @author djc
 */
public record Notification(String note, boolean log) {
    // TODO: multiple notes, scroll speed, color, position, etc.

    public static Notification create(String note, boolean log) {
        return new Notification(note, log);
    }

}
