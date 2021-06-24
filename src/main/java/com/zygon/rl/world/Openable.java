package com.zygon.rl.world;

/**
 *
 * @author zygon
 */
@Deprecated
public class Openable extends EntityWrapper {

    private final Entity openable;

    public Openable(Entity openable) {
        super(openable);
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
