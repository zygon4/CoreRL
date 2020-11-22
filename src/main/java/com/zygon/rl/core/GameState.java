package com.zygon.rl.core;

import java.util.Objects;

/**
 * Goals: immutable, serializable, reasonable performance
 *
 * @author zygon
 */
public class GameState {

    private final World world;

    private GameState(Builder builder) {
        this.world = Objects.requireNonNull(builder.world);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public World getWorld() {
        return world;
    }

    public static class Builder {

        private World world;

        private Builder() {

        }

        private Builder(GameState gameState) {
            this.world = gameState.getWorld();
        }

        public void setWorld(World world) {
            this.world = world;
        }

        public GameState build() {
            return new GameState(this);
        }
    }
}
