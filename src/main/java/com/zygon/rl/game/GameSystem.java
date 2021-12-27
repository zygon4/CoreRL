package com.zygon.rl.game;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 *
 * @author zygon
 */
public abstract class GameSystem implements Function<GameState, GameState> {

    protected static final int REALITY_BUBBLE = 50;
    private final Executor executor = Executors.newFixedThreadPool(3);

    private final GameConfiguration gameConfiguration;

    protected GameSystem(GameConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
    }

    protected final Executor getExecutor() {
        return executor;
    }

    protected final GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }
}
