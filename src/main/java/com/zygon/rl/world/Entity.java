package com.zygon.rl.world;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This was copied from BloodRL, needs to become its own here
 *
 */
public class Entity {

    private final Set<Attribute> attributes;
    private final Set<Behavior> behaviors;

    // Could become a "id" grouping object
    private final String description;
    private final String displayName;
    private final String name;

    private Entity(Builder builder) {
        this.attributes = builder.attributes != null
                ? Collections.unmodifiableSet(builder.attributes) : Collections.emptySet();
        this.behaviors = builder.behaviors != null
                ? Collections.unmodifiableSet(builder.behaviors) : Collections.emptySet();
        this.description = builder.description != null ? builder.description : "";
        this.displayName = builder.displayName != null ? builder.displayName : "";
        this.name = builder.name != null ? builder.name : "";
    }

    public Entity add(Attribute attribute) {
        Set<Attribute> attrs = new HashSet<>(attributes);
        attrs.add(attribute);
        return copy().setAttributes(attributes).build();
    }

    public Entity add(Behavior behavior) {
        Set<Behavior> behvs = new HashSet<>(behaviors);
        behvs.add(behavior);
        return copy().setBehaviors(behvs).build();
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public Set<Attribute> getAttributes(String name) {

        Set<Attribute> attrs = new HashSet<>();

        for (Attribute attr : attributes) {
            if (attr.getName().equals(name)) {
                attrs.add(attr);
            }
        }

        return attrs;
    }

    public String getAttributeValue(String name) {
        return attributes.stream()
                .filter(a -> a.getName().equals(name))
                .map(Attribute::getValue)
                .findFirst().orElse(null);
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.displayName);
        hash = 89 * hash + Objects.hashCode(this.name);
        return hash;
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
        final Entity other = (Entity) obj;
        if (!Objects.equals(this.displayName, other.displayName)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static class Builder {

        private Set<Attribute> attributes;
        private Set<Behavior> behaviors;

        // Could become a "id" grouping object
        private String description;
        private String displayName;
        private String name;

        private Builder() {
        }

        private Builder(Entity entity) {
            this.attributes = entity.attributes;
            this.behaviors = entity.behaviors;
            this.description = entity.description;
            this.displayName = entity.displayName;
            this.name = entity.name;
        }

        public Builder setAttributes(Set<Attribute> attributes) {
            this.attributes = attributes;
            return this;
        }

        // Sets the first attribute it finds
        public Builder setAttributeValue(String name, String value) {
            if (attributes == null) {
                setAttributes(new HashSet<>());
            }
            Set<Attribute> updatedAttributes = new HashSet<>(attributes);

            Attribute toUpdate = updatedAttributes.stream()
                    .filter(a -> a.getName().equals(name))
                    .findFirst().orElse(null);

            if (toUpdate != null) {
                updatedAttributes.remove(toUpdate);
                toUpdate = toUpdate.copy()
                        .setValue(value)
                        .build();
            } else {
                toUpdate = Attribute.builder()
                        .setName(name)
                        .setValue(value)
                        .build();
            }

            updatedAttributes.add(toUpdate);

            return setAttributes(updatedAttributes);
        }

        public Builder setBehaviors(Set<Behavior> behaviors) {
            this.behaviors = behaviors;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder removeAttribute(String name) {
            return setAttributes(this.attributes.stream()
                    .filter(a -> !a.getName().equals(name))
                    .collect(Collectors.toSet()));
        }

        public Entity build() {
            return new Entity(this);
        }
    }
}
