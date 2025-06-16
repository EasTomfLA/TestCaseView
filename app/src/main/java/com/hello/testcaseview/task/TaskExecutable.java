package com.hello.testcaseview.task;

public interface TaskExecutable {
    void execute(Task task, TaskExecutionCallback callback);

    interface TaskExecutionCallback {
        void onTaskCompleted(Task task);
    }
}