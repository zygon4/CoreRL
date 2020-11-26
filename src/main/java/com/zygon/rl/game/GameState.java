package com.zygon.rl.game;

// Would almost prefer a central "world" interface
import com.zygon.rl.world.Regions;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * Goals: immutable, serializable, reasonable performance
 *
 * @author zygon
 */
public class GameState {

    public static class InputContext {

        private final String name;
        private final Set<Input> validInputs;

        private InputContext(Builder builder) {
            this.name = Objects.requireNonNull(builder.name);
            this.validInputs = builder.validInputs != null
                    ? Collections.unmodifiableSet(builder.validInputs) : Collections.emptySet();
        }

        public String getName() {
            return name;
        }

        // I'm not sure if this is that useful yet. Right now a lot of logic is in
        // the subclasses (not great) _when_ it gets moved common, this will be more
        // useful.
        public Set<Input> getValidInputs() {
            return validInputs;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private String name;
            private Set<Input> validInputs;

            public Builder setName(String name) {
                this.name = name;
                return this;
            }

            // Not useful yet
            @Deprecated
            public Builder setValidInputs(Set<Input> validInputs) {
                this.validInputs = validInputs;
                return this;
            }

            public InputContext build() {
                return new InputContext(this);
            }
        }
    }

    private final Stack<InputContext> inputContext;
    private final Regions regions;

    private GameState(Builder builder) {
        Stack<InputContext> stackCopy = new Stack<>();
        if (builder.inputContext != null) {
            stackCopy.addAll(builder.inputContext);
        }
        this.inputContext = stackCopy;
        this.regions = Objects.requireNonNull(builder.regions);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public Stack<InputContext> getInputContext() {
        return inputContext;
    }

    public Regions getRegions() {
        return regions;
    }

    public static class Builder {

        private Stack<InputContext> inputContext;
        private Regions regions;

        private Builder() {

        }

        private Builder(GameState gameState) {
            this.inputContext = gameState.getInputContext();
            this.regions = gameState.getRegions();
        }

        public Builder addInputContext(InputContext inputContext) {
            if (this.inputContext == null) {
                this.inputContext = new Stack<>();
            }
            this.inputContext.push(inputContext);
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

        public Builder setRegions(Regions regions) {
            this.regions = regions;
            return this;
        }

        public GameState build() {
            return new GameState(this);
        }
    }
}
