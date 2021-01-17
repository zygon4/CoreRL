package com.zygon.rl.world;

import com.zygon.rl.world.DamageType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This could be healing if the damage is negative? e.g. holy "damage" healing
 * an angel.
 *
 * @author zygon
 */
public class DamageResolution {

    private final String attacker;
    private final String defender;
    private final boolean miss;
    private final boolean critial;
    private final Map<DamageType, Integer> damageByType = new LinkedHashMap<>();
    private int totalDamage = 0;

    // TODO: damage to weapons/armor/items on person, or even damage
    // to the local area (acid spray, etc.), and status effects like knockdown.
    //
    public DamageResolution(String attacker, String defender, boolean miss, boolean critial) {
        this.attacker = attacker;
        this.defender = defender;
        this.miss = miss;
        this.critial = critial;
        if (miss && critial) {
            throw new IllegalArgumentException();
        }
    }

    // Will override, not add
    public void set(DamageType damage, int ammount) {
        damageByType.put(damage, ammount);
        totalDamage += ammount;
    }

    public Map<DamageType, Integer> getDamageByType() {
        return Collections.unmodifiableMap(damageByType);
    }

    public int getTotalDamage() {
        return totalDamage;
    }

    public boolean isCritial() {
        return critial;
    }

    public boolean isMiss() {
        return miss;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (miss) {
            sb.append(attacker).append(" missed!\n");
        } else {
            if (critial) {
                sb.append("Critical!\n");
            }
            sb.append(attacker).append(" hit ")
                    .append(defender).append(" for ")
                    .append(getTotalDamage()).append(" damage\n  ")
                    .append(damageByType.entrySet().stream()
                            .map(entry -> entry.getKey().name() + " - " + entry.getValue() + " damage")
                            .collect(Collectors.joining("\n  ")));
        }
        return sb.toString();
    }

}
