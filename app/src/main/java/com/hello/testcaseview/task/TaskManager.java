package com.hello.testcaseview.task;

import android.content.Context;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private Context context;
    private static final String RESULT_FILE_SUFFIX = "_result.json";

    public TaskManager(Context context) {
        this.context = context;
    }

    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();

        // 创建测试任务
        tasks.add(createTask("network", "网络连接测试"));
        tasks.add(createTask("bluetooth", "蓝牙功能测试"));
        tasks.add(createTask("camera", "相机功能测试"));
        tasks.add(createTask("storage", "存储空间测试"));
        tasks.add(createTask("battery", "电池性能测试"));
        tasks.add(createTask("default", "默认测试任务"));

        return tasks;
    }

    private Task createTask(String id, String name) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setStatus(Task.STATUS_PENDING);
        task.setResult(Task.RESULT_UNKNOWN);
        return task;
    }

    public void saveTaskResult(Task task) {
        try {
            FileOutputStream fos = context.openFileOutput(
                    task.getId() + RESULT_FILE_SUFFIX, Context.MODE_PRIVATE);

            // 创建JSON格式数据
            JSONObject json = new JSONObject();
            json.put("id", task.getId());
            json.put("name", task.getName());
            json.put("status", task.getStatus());
            json.put("result", task.getResult());
            json.put("resultDetails", task.getResultDetails());
            json.put("timestamp", System.currentTimeMillis());

            fos.write(json.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Task> loadTaskResults() {
        List<Task> results = new ArrayList<>();

        // 读取所有任务结果文件
        String[] files = context.fileList();
        for (String file : files) {
            if (file.endsWith(RESULT_FILE_SUFFIX)) {
                Task task = loadTaskResult(file);
                if (task != null) {
                    results.add(task);
                }
            }
        }
        
        return results;
    }

    private Task loadTaskResult(String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            String jsonStr = new String(buffer);
            JSONObject json = new JSONObject(jsonStr);

            Task task = new Task();
            task.setId(json.getString("id"));
            task.setName(json.getString("name"));
            task.setStatus(json.getInt("status"));
            task.setResult(json.getInt("result"));
            task.setResultDetails(json.getString("resultDetails"));

            return task;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 清除单个任务结果文件
     * @param taskId 任务ID
     * @return 是否成功清除
     */
    public boolean clearTaskResult(String taskId) {
        try {
            String filename = taskId + RESULT_FILE_SUFFIX;
            return context.deleteFile(filename);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清除所有任务结果文件
     */
    public void clearAllTaskResults() {
        String[] files = context.fileList();
        for (String file : files) {
            if (file.endsWith(RESULT_FILE_SUFFIX)) {
                context.deleteFile(file);
            }
        }
    }
    
    /**
     * 检查是否存在任务结果文件
     * @return 如果存在任务结果则返回true，否则返回false
     */
    public boolean hasTaskResults() {
        String[] files = context.fileList();
        for (String file : files) {
            if (file.endsWith(RESULT_FILE_SUFFIX)) {
                return true;
            }
        }
        return false;
    }
}