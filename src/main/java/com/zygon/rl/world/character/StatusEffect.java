package com.zygon.rl.world.character;

/**
 *
 * @author zygon
 */
public final class StatusEffect {

    private final String id;
    private final String displayName;
    private final String displaydescription;
    private final boolean displayToPlayer;
    private final Integer value;

    public StatusEffect(String id, String displayName, String displaydescription,
            boolean displayToPlayer, Integer value) {
        this.id = id;
        this.displayName = displayName;
        this.displaydescription = displaydescription;
        this.displayToPlayer = displayToPlayer;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplaydescription() {
        return displaydescription;
    }

    public boolean displayToPlayer() {
        return displayToPlayer;
    }

    public Integer getValue() {
        return value;
    }

    public StatusEffect setValue(Integer value) {
        return new StatusEffect(id, displayName, displaydescription, displayToPlayer, value);
    }
}
