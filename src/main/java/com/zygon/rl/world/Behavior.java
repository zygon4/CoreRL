package com.zygon.rl.world;

/**
 * Describes an interaction between two entities.
 *
 */
public class Behavior {

    // id?
    private final String description;
    private final String displayName;
    private final String name;

    private Behavior(Builder builder) {
        this.description = builder.description;
        this.displayName = builder.displayName;
        this.name = builder.name;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private String displayName;
        private String description;

        public void setDescription(String description) {
            this.description = description;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Behavior build() {
            return new Behavior(this);
        }
    }
}
