package com.zygon.rl.game;

// Would almost prefer a central "world" interface
import com.zygon.rl.world.World;

import java.util.Objects;
import java.util.Stack;

/**
 * Goals: immutable, serializable, reasonable performance
 *
 * @author zygon
 */
public class GameState {

    public static class InputContext {

        // name is useful for logging only?
        private final String name;
        private final LayerInputHandler handler;

        private InputContext(Builder builder) {
            this.name = Objects.requireNonNull(builder.name);
            this.handler = Objects.requireNonNull(builder.handler);
        }

        public String getName() {
            return name;
        }

        public LayerInputHandler getHandler() {
            return handler;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private String name;
            private LayerInputHandler handler;

            public Builder setName(String name) {
                this.name = name;
                return this;
            }

            public Builder setHandler(LayerInputHandler handler) {
                this.handler = handler;
                return this;
            }

            public InputContext build() {
                return new InputContext(this);
            }
        }
    }

    private final int turnCount;
    private final Stack<InputContext> inputContext;
    private final World world;
    private final GameConfiguration gameConfiguration;

    private GameState(Builder builder) {
        Stack<InputContext> stackCopy = new Stack<>();
        if (builder.inputContext != null) {
            stackCopy.addAll(builder.inputContext);
        }
        if (stackCopy.isEmpty()) {
            stackCopy.add(GameState.InputContext.builder()
                    .setName("DEFAULT")
                    .setHandler(new DefaultOuterActionSupplier(builder.gameConfiguration))
                    .build());
        }
        this.turnCount = builder.turnCount;
        this.inputContext = stackCopy;
        this.world = builder.world;
        this.gameConfiguration = builder.gameConfiguration;
    }

    public static Builder builder(GameConfiguration gameConfiguration) {
        return new Builder(gameConfiguration);
    }

    public Builder copy() {
        return new Builder(this);
    }

    public Stack<InputContext> getInputContext() {
        return inputContext;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public World getWorld() {
        return world;
    }

    // private!!!!
    private GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    public static class Builder {

        private int turnCount = 0;
        private Stack<InputContext> inputContext;
        private World world;
        private GameConfiguration gameConfiguration;

        private Builder(GameConfiguration gameConfiguration) {
            this.gameConfiguration = gameConfiguration;
        }

        private Builder(GameState gameState) {
            this.turnCount = gameState.getTurnCount();
            this.inputContext = gameState.getInputContext();
            this.world = gameState.getWorld();
            this.gameConfiguration = gameState.getGameConfiguration();
        }

        public Builder addInputContext(InputContext inputContext) {
            if (this.inputContext == null) {
                this.inputContext = new Stack<>();
            }
            this.inputContext.push(inputContext);
            return this;
        }

        public Builder addTurnCount() {
            this.turnCount++;
            return this;
        }

        public Builder removeInputContext() {
            // If null, could create empty stack but that would mask a bug
            this.inputContext.pop();
            return this;
        }

        public Builder setInputContext(Stack<InputContext> inputContext) {
            this.inputContext = inputContext;
            return this;
        }

        public Builder setWorld(World world) {
            this.world = world;
            return this;
        }

        public Builder setTurnCount(int turnCount) {
            this.turnCount = turnCount;
            return this;
        }

        public GameState build() {
            return new GameState(this);
        }
    }
}
