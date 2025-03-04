/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.game;

import java.lang.System.Logger.Level;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zygon.rl.util.quest.QuestInfo.QuestContext;
import com.zygon.rl.world.action.DamageAction;
import com.zygon.rl.world.quest.QuestType;

/**
 *
 * @author djc
 */
public class QuestState {

    private static final System.Logger LOGGER = System.getLogger(DamageAction.class.getCanonicalName());

    // heh. no context in quest context - who's is this??
    private final Map<QuestType, Set<QuestContext>> questContext = new HashMap<>();

    public void register(QuestType questType, QuestContext questContext) {

        LOGGER.log(Level.INFO, "Registering quest {0}, {1}",
                new Object[]{questType.name(), questContext});

        Set<QuestContext> context = this.questContext.computeIfAbsent(
                questType, k -> new HashSet<>());
        context.add(questContext);
    }

    public <T extends QuestContext> Set<T> getByType(QuestType questType) {
        return (Set<T>) this.questContext.get(questType);
    }
}
