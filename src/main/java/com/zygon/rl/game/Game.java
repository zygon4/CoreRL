package com.zygon.rl.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Goals: hold data, be serializable
 *
 * @author zygon
 */
public class Game {

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

    public static Builder builder() {
        return new Builder();
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

        //
        // TODO: logging input would make the game re-playable
        //
        // Apply the input
        GameState newState = inputHandler.apply(state, input);

        // Apply the game systems in order
        for (GameSystem gs : gameSystems) {
            newState = gs.apply(newState);
        }

        return copy()
                .setState(newState)
                .build();
    }

    private List<GameSystem> getGameSystems() {
        return gameSystems;
    }

    /*pkg*/ GameConfiguration getConfiguration() {
        return configuration;
    }

    /*pkg*/ GameState getState() {
        return state;
    }

    public static class Builder {

        private List<GameSystem> gameSystems = new ArrayList<>();
        private GameConfiguration configuration;
        private GameState state;

        private Builder() {
            gameSystems.add(new DefaultGameSystem());
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

        public Builder setConfiguration(GameConfiguration configuration) {
            this.configuration = configuration;
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
