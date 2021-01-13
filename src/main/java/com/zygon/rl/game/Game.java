package com.zygon.rl.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Goals: hold data, be serializable
 *
 * @author zygon
 */
public final class Game {

    private static final System.Logger logger = System.getLogger(Game.class.getCanonicalName());

    private final InputHandler inputHandler = new InputHandler();
    private final List<GameSystem> gameSystems;
    private final GameConfiguration configuration;
    private final GameState state;

    private Game(Builder builder) {
        this.gameSystems = builder.gameSystems != null
                ? Collections.unmodifiableList(builder.gameSystems) : Collections.emptyList();
        this.configuration = Objects.requireNonNull(builder.configuration);
        this.state = builder.state != null
                ? builder.state : GameState.builder(configuration).build();
    }

    public static Builder builder(GameConfiguration gameConfiguration) {
        return new Builder(gameConfiguration);
    }

    public Builder copy() {
        return new Builder(this);
    }

    /**
     * The primary function to move the game.
     *
     * @param input the input
     * @return a new game after processing the input.
     */
    public Game turn(Input input) {

        // TODO: not sure.. handle elsewhere!
        if (state.isPlayerDead()) {
            throw new IllegalStateException();
        }

        //
        // TODO: logging input would make the game re-playable as long
        // as the game systems used are seeded and consistent.
        //
        GameState newState = inputHandler.apply(state, input);

        // Apply the game systems in order when there is no game context
        // happening. e.g. if the player is fiddling with menus, don't continue
        // the systems.
        if (newState.getInputContext().size() == 1) {
            for (GameSystem gs : gameSystems) {
                long gameSystemStart = System.nanoTime();
                newState = gs.apply(newState);
                // If they need a name to distinguish, can add later
                logger.log(System.Logger.Level.TRACE, "Game system " + gs.getClass().getCanonicalName()
                        + " " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - gameSystemStart));
            }
        }

        return copy()
                .setState(newState)
                .build();
    }

    private List<GameSystem> getGameSystems() {
        return gameSystems;
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }

    public GameState getState() {
        return state;
    }

    public static class Builder {

        private List<GameSystem> gameSystems = new ArrayList<>();
        private GameConfiguration configuration;
        private GameState state;

        private Builder(GameConfiguration gameConfiguration) {
            this.gameSystems.add(new DefaultGameSystem(gameConfiguration));
            this.gameSystems.add(new SpawnSystem(gameConfiguration));
            this.gameSystems.add(new AISystem(gameConfiguration));
            this.configuration = gameConfiguration;
        }

        private Builder(Game game) {
            this.gameSystems.addAll(game.getGameSystems());
            this.configuration = game.getConfiguration();
            this.state = game.getState();
        }

        public Builder addGameSystem(GameSystem gameSystem) {
            if (gameSystems == null) {
                this.gameSystems = new ArrayList<>();
            }
            this.gameSystems.add(gameSystem);
            return this;
        }

        public Builder setState(GameState state) {
            this.state = state;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }
}
