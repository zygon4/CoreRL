/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.character;

import java.util.List;

import com.zygon.rl.world.action.Action;

/**
 *
 * @author djc
 */
public record AbilityActionSet(List<Action> actions) {

    public static AbilityActionSet create(List<Action> actions) {
        return new AbilityActionSet(actions);
    }
}
