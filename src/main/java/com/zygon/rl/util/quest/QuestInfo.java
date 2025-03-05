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
public class QuestInfo<T> implements TaskInfo<T> {

    private static final int PADDING = 4;

    public interface QuestContext<T> {

        boolean isComplete(T t);

        boolean isSuccess(T t);
    }

    private final String name;
    private final String description;
    private final QuestContext<T> context;
    private final List<TaskInfo<T>> subQuests;

    public QuestInfo(String name, String description, QuestContext<T> context,
            List<TaskInfo<T>> subQuests) {
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
    public boolean isComplete(T t) {
        return context.isComplete(t) && subQuests.stream()
                .map(ti -> ti.isComplete(t))
                .filter(complete -> !complete)
                .findAny().orElse(Boolean.TRUE);
    }

    @Override
    public boolean isSuccess(T t) {
        return context.isSuccess(t) && subQuests.stream()
                .map(ti -> ti.isSuccess(t))
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
    public List<TaskInfo<T>> getAllSubTasks() {
        return subQuests;
    }

    @Override
    public List<TaskInfo<T>> getDependentSubTasks(T t) {

        List<TaskInfo<T>> depSubTasks = new ArrayList<>();

        BiConsumer<TaskInfo<T>, Integer> fn = (TaskInfo<T> ti, Integer depth) -> {

            // deliberate reference check, don't include the top level task (ie 'this')
            if (ti != this && (!ti.isComplete(t) || !ti.isSuccess(t))) {
                depSubTasks.add(ti);
            }
        };

        visitTree(fn, this, 0);

        return depSubTasks;
    }

    public String getDisplayString(T t) {
        StringBuilder sb = new StringBuilder();
        buildToString(t, sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.name + ") " + this.description;
    }

    private static <T> void visitTree(BiConsumer<TaskInfo<T>, Integer> fn,
            TaskInfo<T> taskInfo, int depth) {

        fn.accept(taskInfo, depth);

        taskInfo.getAllSubTasks().forEach(qi -> {
            visitTree(fn, qi, depth + 1);
        });
    }

    private void buildToString(T t, StringBuilder sb) {

        BiConsumer<TaskInfo<T>, Integer> fn = (TaskInfo<T> ti, Integer depth) -> {
            String pad = "";
            for (int i = 0; i < depth * PADDING; i++) {
                pad += " ";
            }
            final String padding = pad;

            sb.append(padding).append(" - ")
                    .append(toDisplay(t, ti))
                    .append("\n");
        };
        visitTree(fn, this, 0);
    }

    private static <T> String toDisplay(T t, TaskInfo<T> questInfo) {
        String status = questInfo.isComplete(t) ? (questInfo.isSuccess(t) ? "[+]" : "[F]") : "[ ]";
        return status + " " + questInfo.getName() + ": " + questInfo.getDescription();
    }
}
