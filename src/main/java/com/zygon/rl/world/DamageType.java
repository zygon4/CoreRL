package com.zygon.rl.world;

/**
 *
 * @author zygon
 */
public enum DamageType {
    Slashing(""),
    Piercing(""),
    Bludgeoning(""),
    Poison(""),
    Acid(""),
    Fire(""),
    Cold(""),
    Radiant(""),
    Necrotic(""),
    Lightning(""),
    Thunder(""),
    Force(""),
    Psychic("");

    private DamageType(String soundEffectId) {
        this.soundEffectId = soundEffectId;
    }
    private final String soundEffectId;
}
