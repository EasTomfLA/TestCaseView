package com.hello.testcaseview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

public class TestExecutor {
    
    private List<Task> taskList;
    private TaskManager taskManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private TestExecutionListener listener;

    public TestExecutor(Context context, List<Task> taskList) {
        this.taskList = taskList;
        this.taskManager = new TaskManager(context);
    }
    
    public interface TestExecutionListener {
        void onTestStarted();
        void onTaskStatusChanged(Task task, int position);
        void onAllTestsCompleted();
    }
    
    public void setTestExecutionListener(TestExecutionListener listener) {
        this.listener = listener;
    }
    
    // 开始所有测试
    public void startAllTests() {
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
            if (listener != null) {
                listener.onAllTestsCompleted();
            }
            return;
        }
        
        // 获取当前任务
        final Task currentTask = taskList.get(index);
        
        // 更新任务状态为运行中
        currentTask.setStatus(Task.STATUS_RUNNING);
        if (listener != null) {
            listener.onTaskStatusChanged(currentTask, index);
        }
        
        // 模拟任务执行 (1-3秒随机时间)
        int executionTime = 1000 + (int)(Math.random() * 2000);
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 模拟任务执行结果
                simulateTaskExecution(currentTask);
                
                // 保存任务结果
                taskManager.saveTaskResult(currentTask);
                
                // 通知任务状态发生变化
                if (listener != null) {
                    listener.onTaskStatusChanged(currentTask, index);
                }
                
                // 执行下一个任务
                executeNextTask(index + 1);
            }
        }, executionTime);
    }
    
    // 模拟任务执行
    private void simulateTaskExecution(Task task) {
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
        
        // 为不同测试项添加特定的测试详情
        switch (task.getId()) {
            case "network":
                if (task.getResult() == Task.RESULT_PASS) {
                    task.setResultDetails("网络连接正常，平均延迟23ms");
                } else if (task.getResult() == Task.RESULT_FAIL) {
                    task.setResultDetails("网络连接失败，请检查网络设置");
                }
                break;
            case "bluetooth":
                if (task.getResult() == Task.RESULT_PASS) {
                    task.setResultDetails("蓝牙功能正常，可以正确配对设备");
                } else if (task.getResult() == Task.RESULT_FAIL) {
                    task.setResultDetails("蓝牙配对失败，请检查蓝牙状态");
                }
                break;
            case "camera":
                if (task.getResult() == Task.RESULT_PASS) {
                    task.setResultDetails("相机功能正常，拍摄与预览均可用");
                } else if (task.getResult() == Task.RESULT_FAIL) {
                    task.setResultDetails("相机预览异常，请检查权限或重启设备");
                }
                break;
            case "storage":
                if (task.getResult() == Task.RESULT_PASS) {
                    task.setResultDetails("存储空间正常，读写速度稳定");
                } else if (task.getResult() == Task.RESULT_FAIL) {
                    task.setResultDetails("存储空间读写异常，速度低于预期");
                }
                break;
            case "battery":
                if (task.getResult() == Task.RESULT_PASS) {
                    task.setResultDetails("电池性能正常，放电速率符合预期");
                } else if (task.getResult() == Task.RESULT_FAIL) {
                    task.setResultDetails("电池温度异常，请检查电池状况");
                }
                break;
        }
    }
}