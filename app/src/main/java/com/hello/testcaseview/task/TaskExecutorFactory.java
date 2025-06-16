package com.hello.testcaseview.task;

import android.content.Context;
import android.os.Handler;

import com.hello.testcaseview.task.executors.BluetoothTaskExecutor;
import com.hello.testcaseview.task.executors.CameraTaskExecutor;
import com.hello.testcaseview.task.executors.DefaultTaskExecutor;
import com.hello.testcaseview.task.executors.NetworkTaskExecutor;
import com.hello.testcaseview.task.executors.StorageTaskExecutor;
import com.hello.testcaseview.task.executors.BatteryTaskExecutor;

public class TaskExecutorFactory {

    private Context context;
    private Handler handler;

    public TaskExecutorFactory(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public TaskExecutable getExecutorForTask(Task task) {
        String taskId = task.getId();

        switch (taskId) {
            case "network":
                return new NetworkTaskExecutor(context, handler);
            case "bluetooth":
                return new BluetoothTaskExecutor(context, handler);
            case "camera":
                return new CameraTaskExecutor(context, handler);
            case "storage":
                return new StorageTaskExecutor(context, handler);
            case "battery":
                return new BatteryTaskExecutor(context, handler);
            default:
                return new DefaultTaskExecutor(handler);
        }
    }
}