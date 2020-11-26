package com.zygon.rl.world;

/**
 *
 * @author zygon
 */
public class Openable {

    private final Entity openable;

    public Openable(Entity openable) {
        this.openable = openable;
    }

    public Openable close() {
        return !isClosed()
                ? new Openable(openable.copy()
                        // TBD: view block and impassable should be specific to certain openables
                        .setAttributeValue(CommonAttributes.CLOSED.name(), Boolean.TRUE.toString())
                        .setAttributeValue(CommonAttributes.IMPASSABLE.name(), Boolean.TRUE.toString())
                        .setAttributeValue(CommonAttributes.VIEW_BLOCK.name(), "1.0")
                        .build())
                : this;
    }

    public Entity getEntity() {
        return openable;
    }

    public boolean isClosed() {
        String closed = openable.getAttributeValue(CommonAttributes.CLOSED.name());
        return closed != null && Boolean.parseBoolean(closed);
    }

    // TBD:
    public boolean isLocked() {
        return false;
    }

    public Openable open() {
        return isClosed()
                ? new Openable(openable.copy()
                        .removeAttribute(CommonAttributes.IMPASSABLE.name())
                        .setAttributeValue(CommonAttributes.CLOSED.name(), Boolean.FALSE.toString())
                        .setAttributeValue(CommonAttributes.VIEW_BLOCK.name(), ".05")
                        .build())
                : this;
    }
}
