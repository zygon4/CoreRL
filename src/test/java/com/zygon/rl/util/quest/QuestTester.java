/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.util.quest;

import java.util.List;
import java.util.stream.Collectors;

import com.zygon.rl.util.quest.QuestInfo.QuestContext;

import org.junit.Test;

/**
 *
 * @author zygon
 */
public class QuestTester {

    private static final class QuestContextImpl implements QuestContext<String> {

        private final boolean isComplete;
        private final boolean isSuccess;

        private QuestContextImpl(boolean isComplete, boolean isSuccess) {
            this.isComplete = isComplete;
            this.isSuccess = isSuccess;
        }

        @Override
        public boolean isComplete(String s) {
            return isComplete;
        }

        @Override
        public boolean isSuccess(String s) {
            return isSuccess;
        }

        public static QuestContext create(boolean isComplete, boolean isSuccess) {
            return new QuestContextImpl(isComplete, isSuccess);
        }
    }

    @Test
    public void main() {

        QuestInfo gatherQuest = new QuestInfo("Gather", "Gather the ingredients for the summoning ritual",
                QuestContextImpl.create(true, true));

        QuestInfo summonQuest = new QuestInfo("Summon", "Perform the summoning ritual",
                QuestContextImpl.create(true, true),
                List.of(gatherQuest));

        QuestInfo weakenQuest = new QuestInfo("Weaken", "Perform the weakening ritual",
                QuestContextImpl.create(true, false));

        QuestInfo trapQuest = new QuestInfo("Trap", "Perform the trapping ritual",
                QuestContextImpl.create(false, false));

        QuestInfo deicideQuest = new QuestInfo("Deicide",
                "Perform the fancy ritual to summon and trap the god, and poke it in the eye.",
                QuestContextImpl.create(false, false),
                List.of(summonQuest, weakenQuest, trapQuest));

        System.out.println(deicideQuest);
        System.out.println("Complete? " + deicideQuest.isComplete(""));
        System.out.println("Successful? " + deicideQuest.isSuccess(""));

        System.out.println(deicideQuest.getDependentSubTasks("").stream()
                .map(ti -> ti.toString())
                .collect(Collectors.joining("\n")));

    }
}
