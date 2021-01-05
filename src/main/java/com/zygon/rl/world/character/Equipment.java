package com.zygon.rl.world.character;

/**
 * The world should probably have a cache of all equipment and use a slidehook
 * pattern.
 *
 * @author zygon
 */
public class Equipment {

    // start small, single weapon only
    // TODO: add items
    private final Weapon weapon;

    public Equipment(Weapon weapon) {
        this.weapon = weapon;
    }

    public Weapon getEquippedWeapon() {
        return weapon;
    }
}
