package io.github.tuanpq.todomvpjava.data.source.local;

import androidx.annotation.NonNull;

import java.util.List;

import io.github.tuanpq.todomvpjava.data.source.Task;
import io.github.tuanpq.todomvpjava.data.source.TasksDataSource;
import io.github.tuanpq.todomvpjava.utility.AppExecutors;

public class TasksLocalDataSource implements TasksDataSource {

    private static volatile TasksLocalDataSource INSTANCE;

    private TasksDAO tasksDAO;

    private AppExecutors appExecutors;

    private TasksLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull TasksDAO tasksDAO) {
        this.appExecutors = appExecutors;
        this.tasksDAO = tasksDAO;
    }

    public static TasksLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull TasksDAO tasksDao) {
        if (INSTANCE == null) {
            synchronized (TasksLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksLocalDataSource(appExecutors, tasksDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull GetTasksCallback callback) {
        Runnable runnable = () -> {
            final List<Task> tasks = tasksDAO.getTasks();
            appExecutors.mainThread().execute(() -> callback.onGetTasksCompleted(tasks));
        };

        appExecutors.localDatabase().execute(runnable);
    }

    @Override
    public void getTask(@NonNull Integer taskId, @NonNull GetTaskCallback callback) {
        Runnable runnable = () -> {
            final Task task = tasksDAO.getTaskById(taskId);
            appExecutors.mainThread().execute(() -> callback.onGetTaskCompleted(task));
        };

        appExecutors.localDatabase().execute(runnable);
    }

    @Override
    public void saveTask(@NonNull Task task, @NonNull SaveTaskCallback callback) {
        Runnable runnable = () -> {
            tasksDAO.insertTask(task);
            appExecutors.mainThread().execute(() -> callback.onSaveTaskCompleted());
        };

        appExecutors.localDatabase().execute(runnable);
    }

    @Override
    public void deleteTask(@NonNull Integer taskId, @NonNull DeleteTaskCallback callback) {
        Runnable runnable = () -> {
            tasksDAO.deleteTaskById(taskId);
            appExecutors.mainThread().execute(() -> callback.onDeleteTaskCompleted());
        };

        appExecutors.localDatabase().execute(runnable);
    }

    @Override
    public void updateTask(@NonNull Task task, @NonNull UpdateTaskCallback callback) {
        Runnable runnable = () -> {
            tasksDAO.updateTask(task);
            appExecutors.mainThread().execute(() -> callback.onUpdateTaskCompleted());
        };

        appExecutors.localDatabase().execute(runnable);
    }
}
