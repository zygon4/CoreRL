package com.zygon.rl.world.character;

import java.util.Random;

import com.zygon.rl.util.DiceRoller;
import com.zygon.rl.world.DamageResolution;
import com.zygon.rl.world.DamageType;

/**
 *
 * @author zygon
 */
public class StatusResolver {

    private final Random random;
    private final DiceRoller dice;

    public StatusResolver(Random random) {
        this.random = random;
        this.dice = new DiceRoller(this.random);
    }

    public DamageResolution resolveStatusDamage(CharacterSheet defender,
            int intensity, DamageType damageType) {

        // TODO: stat check
        int dmg = dice.rollD4() + intensity;

        DamageResolution resolution = new DamageResolution(defender.getName(), false, false, false);
        if (dmg > 0) {
            resolution.set(damageType, dmg);
        }

        return resolution;
    }
}
