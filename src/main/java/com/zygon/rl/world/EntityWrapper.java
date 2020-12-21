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

    public BooleanAttribute getBoolean(String attr) {
        return BooleanAttribute.create(entity.getAttribute(attr));
    }

    public DoubleAttribute getDouble(String attr) {
        return DoubleAttribute.create(entity.getAttribute(attr));
    }

    public Entity getEntity() {
        return entity;
    }

    public String getName() {
        return getEntity().getName();
    }
}
