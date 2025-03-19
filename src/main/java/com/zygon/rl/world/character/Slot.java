package com.zygon.rl.world.character;

/**
 * Starting basic equipment slots, not including wielded items. Most typical two
 * slot items (gloves, etc) will come as an item that takes 2 slots.
 *
 * @author zygon
 */
public enum Slot {
    HEAD("Head"),
    TORSO("Torso"),
    ARM("Arm"),
    HAND("Hand"),
    RING("Ring"),
    LEG("Leg"),
    FOOT("Foot");
    // TODO: amulet, brooch, shoulders, brow, upper vs lower
    // TODO: no single foot/leg

    private final String name;

    private Slot(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the Slot from the given name field (not the enum constant). This
     * is not cached but should be pretty fast.
     *
     * @param name the name of the Slot
     * @return the Slot from the given name field (not the enum constant).
     * @throws EnumConstantNotPresentException if the name does not match one of
     * the Slot "name" fields.
     */
    public static Slot from(String name) throws EnumConstantNotPresentException {
        for (Slot slot : Slot.values()) {
            if (slot.getName().equals(name)) {
                return slot;
            }
        }

        throw new EnumConstantNotPresentException(Slot.class, name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
