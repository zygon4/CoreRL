package com.zygon.rl.world.character;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The world should probably have a cache of all equipment and use a slidehook
 * pattern.
 *
 * @author zygon
 */
public class Equipment {

    private final List<Item> items;
    // Could also have a "equipped" flag on the Item?

    // TODO: we need equipment slots, really, cmon..
    private final Weapon rightHandedWeapon;
    private final Weapon leftHandedWeapon;

    // This has a code smell.....
    private Equipment(List<Item> items, Weapon rightHandedWeapon, Weapon leftHandedWeapon) {
        this.items = items != null
                ? Collections.unmodifiableList(items) : Collections.emptyList();

        this.rightHandedWeapon = rightHandedWeapon;
        this.leftHandedWeapon = leftHandedWeapon;
    }

    public Equipment(List<Item> items) {
        this(items, null, null);
    }

    public Equipment wield(String id, boolean right) {

        if ((right && getEquippedRightWeapon() != null) || getEquippedLeftWeapon() != null) {
            throw new IllegalStateException("Hand not available.");
        }

        List<Item> items = new ArrayList<>(getItems());
        Item item = null;

        int itemIndex;
        for (itemIndex = 0; itemIndex < items.size(); itemIndex++) {
            if (items.get(itemIndex).getTemplate().getId().equals(id)) {
                item = items.remove(itemIndex);
                break;
            }
        }

        if (item == null) {
            throw new IllegalStateException("item " + id + " not available.");
        }

        Equipment equipment = null;

        if (isWeapon(item)) {
            Weapon w = (Weapon) item;
            equipment = new Equipment(items, right ? w : null, !right ? w : null);
        } else {
            // TODO: convert any item to be a weapon
//            equipment = new Equipment(items, rightHandedWeapon, leftHandedWeapon)
        }

        return equipment;
    }

    private static boolean isWeapon(Item item) {
        // TODO: other types of weapons
        String type = item.getTemplate().getType();
        return type.equals("MELEE") || type.equals("RANGED");
    }

    public Weapon getEquippedRightWeapon() {

        // So how about: if the equiped item IS a weapon, then return it,
        // otherwise create a weapon (at equip time) to return
        return rightHandedWeapon;
    }

    public Weapon getEquippedLeftWeapon() {
        return leftHandedWeapon;
    }

    public List<Item> getItems() {
        return items;
    }
}
