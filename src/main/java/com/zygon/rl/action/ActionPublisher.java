package com.zygon.rl.action;

import com.zygon.rl.core.GameState;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 *
 * @author zygon
 */
public class ActionPublisher {

    private final List<BiFunction<GameState, Action, GameState>> actionConsumers;

    public ActionPublisher(List<BiFunction<GameState, Action, GameState>> actionConsumers) {
        this.actionConsumers = actionConsumers != null
                ? Collections.unmodifiableList(actionConsumers) : Collections.emptyList();
    }

    public GameState publish(GameState state, Action action) {
        // TODO: trace logging
        GameState current = state;

        for (BiFunction<GameState, Action, GameState> a : actionConsumers) {
            current = a.apply(current, action);
        }

        return current;
    }
}
