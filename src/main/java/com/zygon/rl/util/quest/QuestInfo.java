/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.util.quest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Quests are in a tree. Unordered in one "Impl" but leaves must be completed
 * first.
 *
 * @author djc
 */
public class QuestInfo implements TaskInfo {

    private static final int PADDING = 4;

    public static interface QuestContext {

        boolean isComplete();

        boolean isSuccess();
    }

    private final String name;
    private final String description;
    private final QuestContext context;
    private final List<TaskInfo> subQuests;

    public QuestInfo(String name, String description, QuestContext context,
            List<TaskInfo> subQuests) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.context = Objects.requireNonNull(context);
        this.subQuests = subQuests != null
                ? Collections.unmodifiableList(subQuests) : Collections.emptyList();
    }

    public QuestInfo(String name, String description, QuestContext context) {
        this(name, description, context, null);
    }

    @Override
    public boolean isComplete() {
        return context.isComplete() && subQuests.stream()
                .map(TaskInfo::isComplete)
                .filter(complete -> !complete)
                .findAny().orElse(Boolean.TRUE);
    }

    @Override
    public boolean isSuccess() {
        return context.isSuccess() && subQuests.stream()
                .map(TaskInfo::isSuccess)
                .filter(success -> !success)
                .findAny().orElse(Boolean.TRUE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<TaskInfo> getAllSubTasks() {
        return subQuests;
    }

    @Override
    public List<TaskInfo> getDependentSubTasks() {

        List<TaskInfo> depSubTasks = new ArrayList<>();

        BiConsumer<TaskInfo, Integer> fn = (TaskInfo t, Integer depth) -> {

            // deliberate reference check, don't include the top level task (ie 'this')
            if (t != this && (!t.isComplete() || !t.isSuccess())) {
                depSubTasks.add(t);
            }
        };

        visitTree(fn, this, 0);

        return depSubTasks;
    }

    public String getDisplayString() {
        StringBuilder sb = new StringBuilder();
        buildToString(sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        return getDisplayString();
    }

    private static void visitTree(BiConsumer<TaskInfo, Integer> fn,
            TaskInfo taskInfo, int depth) {

        fn.accept(taskInfo, depth);

        taskInfo.getAllSubTasks().forEach(qi -> {
            visitTree(fn, qi, depth + 1);
        });
    }

    private void buildToString(StringBuilder sb) {

        BiConsumer<TaskInfo, Integer> fn = (TaskInfo t, Integer depth) -> {
            String pad = "";
            for (int i = 0; i < depth * PADDING; i++) {
                pad += " ";
            }
            final String padding = pad;

            sb.append(padding).append(" - ")
                    .append(toDisplay(t))
                    .append("\n");
        };
        visitTree(fn, this, 0);
    }

    private static String toDisplay(TaskInfo questInfo) {
        String status = questInfo.isComplete() ? (questInfo.isSuccess() ? "[+]" : "[F]") : "[ ]";
        return status + " " + questInfo.getName() + ": " + questInfo.getDescription();
    }
}
