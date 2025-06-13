package com.hello.testcaseview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView taskListView;
    private Button testButton;
    private Button resultButton;
    private TaskAdapter taskAdapter;
    private TaskManager taskManager;
    private List<Task> taskList;
    private Handler handler = new Handler();
    private boolean isTestingInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        taskListView = findViewById(R.id.taskListView);
        testButton = findViewById(R.id.testButton);
        resultButton = findViewById(R.id.resultButton);

        // 初始化TaskManager
        taskManager = new TaskManager(this);
        
        // 获取任务列表
        taskList = taskManager.createTasks();

        // 设置RecyclerView
        taskAdapter = new TaskAdapter(taskList);
        taskListView.setLayoutManager(new LinearLayoutManager(this));
        taskListView.setAdapter(taskAdapter);

        // 设置开始测试按钮
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTestingInProgress) {
                    startAllTests();
                } else {
                    Toast.makeText(MainActivity.this, "测试正在进行中，请稍候...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置查看结果按钮
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTestingInProgress) {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "测试正在进行中，请等待测试完成", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 检查是否有已保存的任务结果
        updateTasksWithSavedResults();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 更新任务状态显示
        updateTasksWithSavedResults();
    }
    
    // 从TaskManager加载已保存的结果并更新任务列表
    private void updateTasksWithSavedResults() {
        List<Task> savedResults = taskManager.loadTaskResults();
        
        // 将保存的结果更新到当前任务列表
        if (savedResults != null && !savedResults.isEmpty()) {
            for (Task task : taskList) {
                for (Task savedTask : savedResults) {
                    if (task.getId().equals(savedTask.getId())) {
                        task.setStatus(savedTask.getStatus());
                        task.setResult(savedTask.getResult());
                        task.setResultDetails(savedTask.getResultDetails());
                        break;
                    }
                }
            }
            
            // 更新UI
            taskAdapter.notifyDataSetChanged();
        }
    }
    
    // 开始所有测试
    private void startAllTests() {
        isTestingInProgress = true;
        testButton.setEnabled(false);
        resultButton.setEnabled(false);
        
        Toast.makeText(this, "开始执行所有测试项目", Toast.LENGTH_SHORT).show();
        
        // 重置所有任务状态
        for (Task task : taskList) {
            task.setStatus(Task.STATUS_PENDING);
            task.setResult(Task.RESULT_UNKNOWN);
            task.setResultDetails("");
        }
        taskAdapter.notifyDataSetChanged();
        
        // 开始执行任务队列
        executeNextTask(0);
    }
    
    // 递归执行任务队列
    private void executeNextTask(final int index) {
        // 检查是否已完成所有任务
        if (index >= taskList.size()) {
            testingCompleted();
            return;
        }
        
        // 获取当前任务
        final Task currentTask = taskList.get(index);
        
        // 更新任务状态为运行中
        currentTask.setStatus(Task.STATUS_RUNNING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                taskAdapter.notifyDataSetChanged();
            }
        });
        
        // 模拟任务执行 (1-3秒随机时间)
        int executionTime = 1000 + (int)(Math.random() * 2000);
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 模拟任务执行结果
                simulateTaskExecution(currentTask);
                
                // 保存任务结果
                taskManager.saveTaskResult(currentTask);
                
                // 更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskAdapter.notifyDataSetChanged();
                    }
                });
                
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
    
    // 所有测试完成
    private void testingCompleted() {
        isTestingInProgress = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testButton.setEnabled(true);
                resultButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "所有测试已完成，可查看详细结果", Toast.LENGTH_LONG).show();
            }
        });
    }
}