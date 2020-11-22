package com.zygon.rl.core;

import com.zygon.rl.action.ActionPublisher;

import java.util.Objects;

/**
 * Goals: hold data, be serializable
 *
 * @author zygon
 */
public class Game {

    private final ActionPublisher actionPublisher;
    private final GameState state;

    private Game(Builder builder) {
        this.actionPublisher = Objects.requireNonNull(builder.actionPublisher);
        this.state = builder.state;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    private ActionPublisher getActionPublisher() {
        return actionPublisher;
    }

    private GameState getState() {
        return state;
    }

    public static class Builder {

        private ActionPublisher actionPublisher;
        private GameState state;

        private Builder() {

        }

        private Builder(Game game) {
            this.actionPublisher = game.getActionPublisher();
            this.state = game.getState();
        }

        public void setActionPublisher(ActionPublisher actionPublisher) {
            this.actionPublisher = actionPublisher;
        }

        public void setState(GameState state) {
            this.state = state;
        }

        public Game build() {
            return new Game(this);
        }

    }
}
