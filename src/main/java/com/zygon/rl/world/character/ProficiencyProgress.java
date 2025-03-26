/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.character;

import com.zygon.rl.data.character.Proficiencies;

/**
 *
 * @author djc
 */
public final class ProficiencyProgress {

    private final String proficiencyId;
    private final int requiredXp;
    private final int xp;

    public ProficiencyProgress(String proficiencyId, int requiredXp, int xp) {
        this.proficiencyId = proficiencyId;
        this.requiredXp = requiredXp;
        this.xp = xp;
    }

    public static ProficiencyProgress create(String proficiencyId,
            int requiredXp) {
        return new ProficiencyProgress(proficiencyId, requiredXp, 0);
    }

    public ProficiencyProgress add(int xp) {
        return new ProficiencyProgress(this.getProficiencyId(), this.getRequiredXp(),
                this.getXp() + xp);
    }

    public String getProficiencyId() {
        return proficiencyId;
    }

    public int getRequiredXp() {
        return requiredXp;
    }

    public int getXp() {
        return xp;
    }

    public boolean levelUp() {
        return this.getXp() >= this.getRequiredXp();
    }

    @Override
    public String toString() {
        return Proficiencies.get(this.getProficiencyId()).getName()
                + ": " + this.getXp() + "/" + this.getRequiredXp();
    }
}
