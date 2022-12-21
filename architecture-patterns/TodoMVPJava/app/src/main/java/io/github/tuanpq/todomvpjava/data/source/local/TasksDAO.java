package io.github.tuanpq.todomvpjava.data.source.local;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.github.tuanpq.todomvpjava.data.source.Task;

public interface TasksDAO {
    @Query("SELECT * FROM Tasks")
    List<Task> getTasks();

    @Query("SELECT * FROM Tasks WHERE taskId = :taskId")
    Task getTaskById(Integer taskId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    @Update
    int updateTask(Task task);

    @Query("DELETE FROM Tasks WHERE taskId = :taskId")
    int deleteTaskById(Integer taskId);
}
