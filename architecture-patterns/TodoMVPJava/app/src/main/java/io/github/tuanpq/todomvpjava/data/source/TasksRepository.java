package io.github.tuanpq.todomvpjava.data.source;

import androidx.annotation.NonNull;

public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource tasksLocalDataSource;

    private TasksRepository(@NonNull TasksDataSource tasksLocalDataSource) {
        this.tasksLocalDataSource = tasksLocalDataSource;
    }

    public static TasksRepository getInstance(TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksLocalDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull GetTasksCallback callback) {

    }

    @Override
    public void getTask(@NonNull Integer taskId, @NonNull GetTaskCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task, @NonNull SaveTaskCallback callback) {

    }

    @Override
    public void deleteTask(@NonNull Integer taskId, @NonNull DeleteTaskCallback callback) {

    }

    @Override
    public void updateTask(@NonNull Task task, @NonNull UpdateTaskCallback callback) {

    }

}
