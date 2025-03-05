/*
 * Copyright Liminal Data Systems 2025
 */
package com.zygon.rl.util.quest;

import java.util.List;

/**
 *
 * @author djc
 */
public interface TaskInfo<T> {

    /**
     * Returns the name of the task.
     *
     * @return the name of the task.
     */
    String getName();

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    String getDescription();

    /**
     * Returns true if the task is complete (regardless of result), false
     * otherwise.
     *
     * @return true if the task is complete (regardless of result), false
     * otherwise.
     */
    boolean isComplete(T t);

    /**
     * Returns true if the task is completed successfully, false otherwise.
     *
     * @return true if the task is completed successfully, false otherwise.
     */
    boolean isSuccess(T t);

    /**
     * Returns a list of the immediate sub tasks.
     *
     * @return a list of the immediate sub tasks.
     */
    List<TaskInfo<T>> getAllSubTasks();

    /**
     * Returns a list of all subtasks.
     *
     * @return a list of all subtasks.
     */
    List<TaskInfo<T>> getDependentSubTasks(T t);
}
