package com.zygon.rl.world;

/**
 *
 * @author zygon
 */
public enum DamageType {
    Slashing(""),
    Piercing(""),
    Bleeding(""), // I don't love this one.. but I'm not sure what to call it..
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
