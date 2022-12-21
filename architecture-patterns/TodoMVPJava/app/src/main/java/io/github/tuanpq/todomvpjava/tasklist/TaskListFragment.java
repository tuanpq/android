package io.github.tuanpq.todomvpjava.tasklist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.tuanpq.todomvpjava.R;
import io.github.tuanpq.todomvpjava.base.BaseFragment;
import io.github.tuanpq.todomvpjava.data.source.Task;

public class TaskListFragment extends BaseFragment implements TaskListContract.View {

    private TaskListContract.Presenter presenter;

    private TaskListRecyclerViewAdapter taskListAdapter;

    private TaskListModel taskListModel;

    public TaskListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            taskListModel = new TaskListModel();
            taskListModel.taskList = new ArrayList<>(0);
            taskListAdapter = new TaskListRecyclerViewAdapter(taskListModel);
            recyclerView.setAdapter(taskListAdapter);

            presenter.loadTaskList();
        }
        return view;
    }

    @Override
    public void setPresenter(TaskListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showTasks(List<Task> tasks) {
        taskListModel.taskList = tasks;
        taskListAdapter.updateData(taskListModel);
    }

}