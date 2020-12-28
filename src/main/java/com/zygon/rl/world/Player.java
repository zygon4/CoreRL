package com.zygon.rl.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author zygon
 */
@Deprecated // use world.character package
public class Player extends EntityWrapper {

    private final List<Item> items;

    private Player(Builder builder) {
        super(builder.playerEntity);
        // TODO: items won't work like this, they need to be embedded
        // inside the player entity
        this.items = Collections.unmodifiableList(builder.items);
    }

    public List<Item> getItems() {
        return items;
    }

    public static Builder create(Entity playerEntity) {
        return new Builder(playerEntity);
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static class Builder {

        private final Entity playerEntity;
        private List<Item> items = new ArrayList<>();

        private Builder(Player player) {
            this.playerEntity = player.getEntity();
            this.items = player.getItems();
        }

        private Builder(Entity entity) {
            this.playerEntity = Objects.requireNonNull(entity);
        }

        public Builder add(Item item) {
            items.add(item);
            return this;
        }

        // TODO: behaviors
        public Player build() {
            return new Player(this);
        }
    }
}
