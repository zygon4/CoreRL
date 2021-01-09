package com.zygon.rl.world;

/**
 * A wrapper around an entity to provide a better interface. This may become a
 * common pattern.
 */
class EntityWrapper {

    private final Entity entity;

    protected EntityWrapper(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getName() {
        return getEntity().getName();
    }
}
