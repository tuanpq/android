package io.github.tuanpq.todomvpjava.data.source;

import androidx.annotation.NonNull;

import java.util.List;

import io.github.tuanpq.todomvpjava.data.source.Task;

public interface TasksDataSource {
    interface GetTasksCallback {
        void onGetTasksCompleted(List<Task> tasks);
    }

    interface GetTaskCallback {
        void onGetTaskCompleted(Task task);
    }

    interface DeleteTaskCallback {
        void onDeleteTaskCompleted();
    }

    interface SaveTaskCallback {
        void onSaveTaskCompleted();
    }

    interface UpdateTaskCallback {
        void onUpdateTaskCompleted();
    }

    void getTasks(@NonNull GetTasksCallback callback);

    void getTask(@NonNull Integer taskId, @NonNull GetTaskCallback callback);

    void saveTask(@NonNull Task task, @NonNull SaveTaskCallback callback);

    void deleteTask(@NonNull Integer taskId, @NonNull DeleteTaskCallback callback);

    void updateTask(@NonNull Task task, @NonNull UpdateTaskCallback callback);
}
