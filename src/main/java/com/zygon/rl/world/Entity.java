package com.zygon.rl.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Primary entity component.
 *
 * Keep in mind for future impl, entities should be anything from players,
 * items, other things like furniture? or even doors/windows?
 *
 *
 */
public class Entity {

    private final UUID id;
    private final String name;
    private final String description;
    private final Location origin;
    private final Location location;
    private final Map<String, Attribute> attributes;

    private final Set<Behavior> behaviors;

    private Entity(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = builder.name;
        this.description = builder.description != null ? builder.description : "";
        this.origin = builder.origin;
        this.location = builder.location;

        this.attributes = builder.attributes != null
                ? Collections.unmodifiableMap(builder.attributes) : Collections.emptyMap();
        this.behaviors = builder.behaviors != null
                ? Collections.unmodifiableSet(builder.behaviors) : Collections.emptySet();
    }

    public Entity add(Attribute attribute) {
        Map<String, Attribute> attrs = new HashMap<>(attributes);
        attrs.put(attribute.getName(), attribute);
        return copy().setAttributes(attributes).build();
    }

    public Entity add(Behavior behavior) {
        Set<Behavior> behvs = new HashSet<>(behaviors);
        behvs.add(behavior);
        return copy().setBehaviors(behvs).build();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }

    public Location getOrigin() {
        return origin;
    }

    public Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    public Attribute getAttribute(String name) {
        return attributes.get(name);
    }

    private Map<String, Attribute> getAttributes() {
        return attributes;
    }

    public String getAttributeValue(String name) {
        return getAttribute(name).getValue();
    }

    private Set<Behavior> getBehaviors() {
        return behaviors;
    }

    public boolean hasAttribute(String name) {
        return getAttribute(name) != null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        if (!Objects.equals(this.id, other.id)) {
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

    // needs more work!
    public static class Builder {

        private UUID id;
        private String name;
        private String description;
        private Location origin;
        private Location location;
        private Map<String, Attribute> attributes;

        private Set<Behavior> behaviors;

        private Builder() {
        }

        private Builder(Entity entity) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.description = entity.getDescription();
            this.origin = entity.getOrigin();
            this.location = entity.getLocation();
            this.attributes = new HashMap<>(entity.getAttributes());
            this.behaviors = entity.getBehaviors();
        }

        public Builder addAttributes(Attribute attribute) {
            if (attributes == null) {
                setAttributes(new HashMap<>());
            }
            attributes.put(attribute.getName(), attribute);
            return this;
        }

        public Builder setAttributes(Map<String, Attribute> attributes) {
            this.attributes = attributes;
            return this;
        }

        // Sets the first attribute it finds
        public Builder setAttributeValue(String name, String value) {
            if (attributes == null) {
                setAttributes(new HashMap<>());
            }

            Attribute attr = this.attributes.get(name);
            if (attr == null) {
                attr = Attribute.builder()
                        .setName(name)
                        .setValue(value)
                        .build();
            } else {
                attr = attr.copy()
                        .setValue(value)
                        .build();
            }

            this.attributes.put(name, attr);

            return this;
        }

        public Builder setBehaviors(Set<Behavior> behaviors) {
            this.behaviors = behaviors;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder setLocation(Location location) {
            this.location = location;
            return this;
        }

        public Builder setOrigin(Location origin) {
            this.origin = origin;
            return this;
        }

        public Builder removeAttribute(String name) {
            this.attributes.remove(name);
            return this;
        }

        public Entity build() {
            return new Entity(this);
        }
    }
}
