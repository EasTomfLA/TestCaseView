package com.hello.testcaseview.task.executors;

import android.os.Handler;

import com.hello.testcaseview.task.Task;
import com.hello.testcaseview.task.TaskExecutable;

public class DefaultTaskExecutor implements TaskExecutable {

    private Handler handler;

    public DefaultTaskExecutor(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void execute(final Task task, final TaskExecutionCallback callback) {
        // 模拟默认测试行为
        int executionTime = 1000 + (int)(Math.random() * 2000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 随机生成测试结果
                int randomResult = (int) (Math.random() * 100);

                if (randomResult < 70) {
                    // 通过 (70%几率)
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_PASS);
                    task.setResultDetails("测试通过，所有功能正常");
                } else if (randomResult < 90) {
                    // 失败 (20%几率)
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_FAIL);
                    task.setResultDetails("测试失败，功能异常");
                } else {
                    // 错误 (10%几率)
                    task.setStatus(Task.STATUS_ERROR);
                    task.setResult(Task.RESULT_ERROR);
                    task.setResultDetails("测试过程发生异常，无法完成测试");
                }

                // 通知执行完成
                callback.onTaskCompleted(task);
            }
        }, executionTime);
    }
}