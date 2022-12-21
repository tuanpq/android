package io.github.tuanpq.todomvpjava.tasklist;

import androidx.annotation.NonNull;

import java.util.List;

import io.github.tuanpq.todomvpjava.base.BasePresenter;
import io.github.tuanpq.todomvpjava.base.BaseView;
import io.github.tuanpq.todomvpjava.data.source.Task;

public class TaskListContract {
    interface View extends BaseView<Presenter> {
        void showTasks(List<Task> tasks);
    }

    interface Presenter extends BasePresenter {
        void loadTaskList();
        void addNewTask();
        void openTaskDetail(@NonNull Task task);
    }
}
