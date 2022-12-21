package io.github.tuanpq.todomvpjava.tasklist;

import androidx.annotation.NonNull;

import io.github.tuanpq.todomvpjava.data.source.Task;
import io.github.tuanpq.todomvpjava.data.source.TasksRepository;

public class TaskListPresenter implements TaskListContract.Presenter {

    private final TasksRepository tasksRepository;

    private final TaskListContract.View taskListView;

    public TaskListPresenter(TasksRepository tasksRepository, TaskListContract.View taskListView) {
        this.tasksRepository = tasksRepository;
        this.taskListView = taskListView;
    }

    @Override
    public void start() {
        loadTaskList();
    }

    @Override
    public void loadTaskList() {
        tasksRepository.getTasks(tasks -> {
            taskListView.showTasks(tasks);
        });
    }

    @Override
    public void addNewTask() {

    }

    @Override
    public void openTaskDetail(@NonNull Task requestedTask) {

    }
}
