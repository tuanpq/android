package io.github.tuanpq.todomvpjava.data.source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public final class Task {

    public enum Status {
        TODO,   // 0
        DOING,  // 1
        DONE;   // 2

        public static Status fromInteger(int x) {
            switch(x) {
                case 0:
                    return TODO;
                case 1:
                    return DOING;
                case 2:
                    return DONE;
            }
            return null;
        }
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "taskId")
    private Integer id;

    @Nullable
    @ColumnInfo(name = "title")
    private String title;

    @Nullable
    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "status")
    private Integer status;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status.ordinal();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return Status.fromInteger(status);
    }

    public void setStatus(Status status) {
        this.status = status.ordinal();
    }

}
