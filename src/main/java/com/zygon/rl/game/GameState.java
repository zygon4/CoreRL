package com.zygon.rl.game;

import java.util.Objects;
import java.util.Stack;

import com.zygon.rl.world.World;

/**
 * Goals: immutable, serializable, reasonable performance
 *
 * @author zygon
 */
public class GameState {

    // I think the real end game will be a more customized way to view.
    // The game state tells us more specifically what should be rendered
    // and how PLUS the inputs. This "prompt" feels like a bit of hack,
    // or leaky.
    // Maybe just broad names like
    // "inventory" - show inv
    // "primary" - game screen (map)
    // "abilities" - show abilities
    // "status" - show full status
    public static enum InputContextPrompt {
        @Deprecated
        DIRECTION,
        @Deprecated
        LIST,
        @Deprecated
        MODAL,
        @Deprecated
        NONE,
        // Starting the above idea here:
        DIALOG,
        INVENTORY,
        PRIMARY
    }

    // TODO: This is going to need to hold more info to teach outsiders how to
    // render this info. E.g. the ability menu needs to prompt for the specific
    // ability actions with name/context for each ability. The targetting
    // prompt needs to know the names/locations of each option, etc.
    public static class InputContext {

        // name is useful for logging only?
        private final String name;
        private final LayerInputHandler handler;
        private final InputContextPrompt prompt;

        private InputContext(Builder builder) {
            this.name = Objects.requireNonNull(builder.name);
            this.handler = Objects.requireNonNull(builder.handler);
            this.prompt = builder.prompt != null
                    ? builder.prompt : GameState.InputContextPrompt.NONE;
        }

        public String getName() {
            return name;
        }

        public LayerInputHandler getHandler() {
            return handler;
        }

        public InputContextPrompt getPrompt() {
            return prompt;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private String name;
            private LayerInputHandler handler;
            private InputContextPrompt prompt;

            public Builder setName(String name) {
                this.name = name;
                return this;
            }

            public Builder setHandler(LayerInputHandler handler) {
                this.handler = handler;
                return this;
            }

            public Builder setPrompt(InputContextPrompt prompt) {
                this.prompt = prompt;
                return this;
            }

            public InputContext build() {
                return new InputContext(this);
            }
        }
    }

    private final int turnCount;
    private final GameLog gameLog;
    private final Notification notification;
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
                    .setHandler(new DefaultOuterInputHandler(builder.gameConfiguration))
                    .setPrompt(InputContextPrompt.PRIMARY)
                    .build());
        }
        this.turnCount = builder.turnCount;
        this.gameLog = builder.gameLog != null ? builder.gameLog : new GameLog();
        this.notification = builder.notification;
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

    public GameLog getLog() {
        return gameLog;
    }

    public Notification getNotification() {
        return notification;
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

    public boolean isPlayerDead() {
        // TODO: for now the player is removed from the game (to be eventually
        // replaced with a corpse). So we only expect the "== null" case to be active.
        return getWorld().getPlayer() == null || getWorld().getPlayer().isDead();
    }

    public GameState log(String message) {
        return copy().addLog(message).build();
    }

    // private!!!!
    private GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    public static class Builder {

        private int turnCount = 0;
        private GameLog gameLog;
        private Notification notification;
        private Stack<InputContext> inputContext;
        private World world;
        private GameConfiguration gameConfiguration;

        private Builder(GameConfiguration gameConfiguration) {
            this.gameConfiguration = gameConfiguration;
        }

        private Builder(GameState gameState) {
            this.turnCount = gameState.getTurnCount();
            this.gameLog = gameState.getLog();
            this.notification = gameState.getNotification();
            this.inputContext = gameState.getInputContext();
            this.world = gameState.getWorld();
            this.gameConfiguration = gameState.getGameConfiguration();
        }

        public Builder addLog(String message) {
            if (this.gameLog == null) {
                this.gameLog = new GameLog();
            }
            this.gameLog = this.gameLog.add(message);
            return this;
        }

        public Builder setNotification(Notification notification) {
            this.notification = notification;
            return this;
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
