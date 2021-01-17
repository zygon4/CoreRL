package com.zygon.rl.game;

import java.util.function.Function;

/**
 *
 * @author zygon
 */
public abstract class GameSystem implements Function<GameState, GameState> {

    protected static final int REALITY_BUBBLE = 50;

    private final GameConfiguration gameConfiguration;

    protected GameSystem(GameConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
    }

    protected final GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }
}
