package com.hello.testcaseview;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hello.testcaseview.task.ResultActivity;
import com.hello.testcaseview.task.Task;
import com.hello.testcaseview.task.TaskAdapter;
import com.hello.testcaseview.task.TaskManager;
import com.hello.testcaseview.task.TestExecutor;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TestExecutor.TestExecutionListener {

    private RecyclerView taskListView;
    private Button testButton;
    private Button resultButton;
    private TaskAdapter taskAdapter;
    private TaskManager taskManager;
    private TestExecutor testExecutor;
    private List<Task> taskList;
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

        // 初始化TestExecutor
        testExecutor = new TestExecutor(this, taskList);
        testExecutor.setTestExecutionListener(this);

        // 设置RecyclerView
        taskAdapter = new TaskAdapter(taskList);
        taskListView.setLayoutManager(new LinearLayoutManager(this));
        taskListView.setAdapter(taskAdapter);

        // 设置开始测试按钮
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTestingInProgress) {
                    testExecutor.startAllTests();
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

    // TestExecutionListener 实现
    @Override
    public void onTestStarted() {
        isTestingInProgress = true;
        testButton.setEnabled(false);
        resultButton.setEnabled(false);
        Toast.makeText(this, "开始执行所有测试项目", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskStatusChanged(Task task, int position) {
        // 更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                taskAdapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void onAllTestsCompleted() {
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