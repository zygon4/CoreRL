package com.zygon.rl.world;

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

    private final String defender;
    private final boolean miss;
    private final boolean critical;
    private final Map<DamageType, Integer> damageByType = new LinkedHashMap<>();
    private final boolean xpGained;
    private int totalDamage = 0;

    // TODO: damage to weapons/armor/items on person, or even damage
    // to the local area (acid spray, etc.), and status effects like knockdown.
    //
    public DamageResolution(String defender, boolean miss, boolean critical,
            boolean xpGained) {
        this.defender = defender;
        this.miss = miss;
        this.critical = critical;
        if (miss && critical) {
            throw new IllegalArgumentException();
        }
        this.xpGained = xpGained;
    }

    // Will override, not add
    public void set(DamageType damage, int ammount) {
        damageByType.put(damage, ammount);
        totalDamage += ammount;
    }

    public Map<DamageType, Integer> getDamageByType() {
        return Collections.unmodifiableMap(damageByType);
    }

    public String getDefender() {
        return defender;
    }

    public int getTotalDamage() {
        return totalDamage;
    }

    public boolean isCritical() {
        return critical;
    }

    public boolean isMiss() {
        return miss;
    }

    public boolean isXpGained() {
        return xpGained;
    }

    protected String getHitMessage() {
        return "HIT";
    }

    protected String getMissMessage() {
        return "MISS";
    }

    protected String getCritMessage() {
        return "CRITICAL";
    }

    protected String getDamageMessage() {

        StringBuilder sb = new StringBuilder();
        if (isMiss()) {
            sb.append(getMissMessage()).append(" ");
        } else {
            if (isCritical()) {
                sb.append(getCritMessage()).append(" ");
            } else {
                sb.append(getHitMessage()).append(" ");
            }
            sb.append(getDefender()).append("\n")
                    .append(" for ")
                    .append(getTotalDamage())
                    .append(" damage\n ")
                    .append(getDamageByType().entrySet().stream()
                            .map(entry -> entry.getKey().name() + " - " + entry.getValue() + " damage")
                            .collect(Collectors.joining("\n  ")));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getDamageMessage();
    }
}
