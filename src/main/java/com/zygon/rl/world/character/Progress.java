/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.world.character;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author djc
 */
public final class Progress {

    /**
     * Required xp to level up a proficiency from 0 to 1. The rest is a scaling
     * factor. This ought to be moved to a config.
     */
    private static final int LVL1_XP = 100;
    private final Map<String, ProficiencyProgress> proficiencyProgress;

    private Progress(Map<String, ProficiencyProgress> proficiencyProgress) {
        this.proficiencyProgress = Collections.unmodifiableMap(proficiencyProgress);
    }

    public static Progress create() {
        return new Progress(new TreeMap<>());
    }

    public Progress add(String proficiencyId, int xp) {
        Map<String, ProficiencyProgress> addProgress = new TreeMap<>(
                this.proficiencyProgress);

        ProficiencyProgress progress = addProgress
                .computeIfAbsent(proficiencyId,
                        k -> ProficiencyProgress.create(proficiencyId, LVL1_XP));

        addProgress.put(proficiencyId, progress.add(xp));
        return new Progress(addProgress);
    }

    public ProficiencyProgress getProficiencyProgress(final String proficiencyId) {
        return proficiencyProgress.get(proficiencyId);
    }

    public Progress set(ProficiencyProgress prog) {
        Map<String, ProficiencyProgress> addProgress = new TreeMap<>(
                this.proficiencyProgress);
        addProgress.put(prog.getProficiencyId(), prog);
        return new Progress(addProgress);
    }

    @Override
    public String toString() {
        return proficiencyProgress.values().stream()
                .map(ProficiencyProgress::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }
}
