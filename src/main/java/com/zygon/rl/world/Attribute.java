package com.zygon.rl.world;

import java.util.Objects;

/**
 * Attribute
 *
 */
public class Attribute {

    private final String name;
    private final String description;
    private final String value;

    protected Attribute(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.value = builder.value;
    }

    public Builder copy() {
        return new Builder(this);
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public String toString() {
        return String.join(",",
                getName() != null ? getName() : "",
                getDescription() != null ? getDescription() : "",
                getValue() != null ? getValue() : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private String description;
        private String value;

        private Builder() {
        }

        private Builder(Attribute attribute) {
            this.name = attribute.getName();
            this.description = attribute.getDescription();
            this.value = attribute.getValue();
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Attribute build() {
            return new Attribute(this);
        }
    }
}
