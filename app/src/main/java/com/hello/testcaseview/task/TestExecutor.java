package com.hello.testcaseview.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

public class TestExecutor {

    private List<Task> taskList;
    private TaskManager taskManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private TestExecutionListener listener;
    private TaskExecutorFactory executorFactory;
    private Context context;
    private boolean isRunning = false;

    public TestExecutor(Context context, List<Task> taskList) {
        this.taskList = taskList;
        this.taskManager = new TaskManager(context);
        this.context = context;
        this.executorFactory = new TaskExecutorFactory(context, handler);
    }

    public interface TestExecutionListener {
        void onTestStarted();
        void onTaskStatusChanged(Task task, int position);
        void onAllTestsCompleted();
    }

    public void setTestExecutionListener(TestExecutionListener listener) {
        this.listener = listener;
    }

    public boolean isRunning() {
        return isRunning;
    }

    // 开始所有测试
    public void startAllTests() {
        // 防止重复启动
        if (isRunning) {
            return;
        }

        isRunning = true;

        // 通知开始测试
        if (listener != null) {
            listener.onTestStarted();
        }

        // 重置所有任务状态
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            task.setStatus(Task.STATUS_PENDING);
            task.setResult(Task.RESULT_UNKNOWN);
            task.setResultDetails("");
            // 通知任务状态发生变化
            if (listener != null) {
                listener.onTaskStatusChanged(task, i);
            }
        }

        // 开始执行任务队列
        executeNextTask(0);
    }

    // 递归执行任务队列
    private void executeNextTask(final int index) {
        // 检查是否已完成所有任务
        if (index >= taskList.size()) {
            isRunning = false;
            if (listener != null) {
                listener.onAllTestsCompleted();
            }
            return;
        }

        // 获取当前任务
        final Task currentTask = taskList.get(index);
        final int currentIndex = index;

        // 更新任务状态为运行中
        currentTask.setStatus(Task.STATUS_RUNNING);
        if (listener != null) {
            listener.onTaskStatusChanged(currentTask, currentIndex);
        }

        // 获取任务对应的执行器
        TaskExecutable executor = executorFactory.getExecutorForTask(currentTask);

        // 执行任务
        executor.execute(currentTask, new TaskExecutable.TaskExecutionCallback() {
            @Override
            public void onTaskCompleted(Task task) {
                // 保存任务结果
                taskManager.saveTaskResult(task);

                // 通知任务状态发生变化
                if (listener != null) {
                    listener.onTaskStatusChanged(task, currentIndex);
                }

                // 执行下一个任务
                executeNextTask(currentIndex + 1);
            }
        });
    }
}