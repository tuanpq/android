package io.github.tuanpq.todomvpjava.utility;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private final Executor localDatabase;
    private final Executor mainThread;

    @VisibleForTesting
    AppExecutors(Executor localDatabase, Executor mainThread) {
        this.localDatabase = localDatabase;
        this.mainThread = mainThread;
    }

    public AppExecutors() {
        this(new LocalDatabaseExecutor(), new MainThreadExecutor());
    }

    public Executor localDatabase() {
        return localDatabase;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    private static class LocalDatabaseExecutor implements Executor {
        private final Executor executor;

        public LocalDatabaseExecutor() {
            executor = Executors.newSingleThreadExecutor();
        }

        @Override
        public void execute(@NonNull Runnable command) {
            executor.execute(command);
        }
    }
}
