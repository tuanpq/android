package io.github.tuanpq.todomvpjava.tasklist;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import io.github.tuanpq.todomvpjava.data.source.Task;
import io.github.tuanpq.todomvpjava.databinding.FragmentTaskItemBinding;

public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.ViewHolder> {

    private TaskListModel taskListModel;

    public TaskListRecyclerViewAdapter(TaskListModel taskListModel) {
        this.taskListModel = taskListModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentTaskItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Task task = taskListModel.taskList.get(position);
        holder.task = task;
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
    }

    @Override
    public int getItemCount() {
        return taskListModel.taskList.size();
    }

    public void updateData(TaskListModel taskListModel) {
        if (taskListModel == null || taskListModel.taskList == null) {
            return;
        }
        this.taskListModel = taskListModel;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView taskIdTextView;
        public final TextView titleTextView;
        public final TextView descriptionTextView;
        public final TextView statusTextView;
        public Task task;

        public ViewHolder(FragmentTaskItemBinding binding) {
            super(binding.getRoot());
            taskIdTextView = binding.taskID;
            titleTextView = binding.title;
            descriptionTextView = binding.description;
            statusTextView = binding.status;
        }
    }
}