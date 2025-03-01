package com.zygon.rl.world;

/**
 * Damage from something to something.
 *
 * @author zygon
 */
public class SourcedDamageResolution extends DamageResolution {

    private final String attacker;

    // TODO: damage to weapons/armor/items on person, or even damage
    // to the local area (acid spray, etc.), and status effects like knockdown.
    //
    public SourcedDamageResolution(String attacker, String defender,
            boolean miss, boolean critial) {
        super(defender, miss, critial);
        this.attacker = attacker;
    }

    @Override
    protected String getCritMessage() {
        return "Critical!";
    }

    @Override
    protected String getMissMessage() {
        return attacker + " missed!";
    }

    @Override
    protected String getDamageMessage() {
        return attacker + " hit " + super.getDamageMessage();
    }
}
