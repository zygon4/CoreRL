package com.zygon.rl.game;

import java.util.Objects;

/**
 * Goals: hold data, be serializable
 *
 * @author zygon
 */
public class Game {

    private final InputHandler inputHandler;
    private final GameConfiguration configuration;
    private final GameState state;

    private Game(Builder builder) {
        this.inputHandler = Objects.requireNonNull(builder.inputHandler);
        this.configuration = Objects.requireNonNull(builder.configuration);
        this.state = Objects.requireNonNull(builder.state);
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

        GameState newState = inputHandler.apply(state, input);

        newState = newState.copy()
                .addTurnCount()
                .build();

        return copy()
                .setState(newState)
                .build();
    }

    private InputHandler getInputHandler() {
        return inputHandler;
    }

    /*pkg*/ GameConfiguration getConfiguration() {
        return configuration;
    }

    /*pkg*/ GameState getState() {
        return state;
    }

    public static class Builder {

        private InputHandler inputHandler;
        private GameConfiguration configuration;
        private GameState state;

        private Builder() {

        }

        private Builder(Game game) {
            this.inputHandler = game.getInputHandler();
            this.configuration = game.getConfiguration();
            this.state = game.getState();
        }

        public Builder setInputHandler(InputHandler inputHandler) {
            this.inputHandler = inputHandler;
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
