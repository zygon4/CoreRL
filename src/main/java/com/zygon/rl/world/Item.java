package com.zygon.rl.world;

import java.util.Objects;

/**
 *
 * @author zygon
 */
public class Item extends EntityWrapper {

    private Item(Builder builder) {
        super(builder.entity);
    }

    public static Builder create(Entity entity) {
        return new Builder(entity);
    }

    public static class Builder {

        private final Entity entity;

        private Builder(Entity entity) {
            this.entity = Objects.requireNonNull(entity);
        }

        public Builder add(Attribute attr) {
            entity.add(attr);
            return this;
        }

        // TODO: behaviors
        public Item build() {
            return new Item(this);
        }
    }
}
