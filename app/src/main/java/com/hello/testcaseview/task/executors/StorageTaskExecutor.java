package com.hello.testcaseview.task.executors;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;

import com.hello.testcaseview.task.Task;
import com.hello.testcaseview.task.TaskExecutable;

import java.io.File;

public class StorageTaskExecutor implements TaskExecutable {

    private Context context;
    private Handler handler;

    public StorageTaskExecutor(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void execute(final Task task, final TaskExecutable.TaskExecutionCallback callback) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取存储空间信息
                    File path = Environment.getExternalStorageDirectory();
                    StatFs stat = new StatFs(path.getPath());
                    long blockSize = stat.getBlockSizeLong();
                    long availableBlocks = stat.getAvailableBlocksLong();
                    long freeSpace = availableBlocks * blockSize;

                    // 模拟读写速度测试
                    int writeSpeed = 10 + (int)(Math.random() * 100); // MB/s

                    if (freeSpace < 1024 * 1024 * 100 || writeSpeed < 30) { // 少于100MB可用或写速小于30MB/s
                        task.setStatus(Task.STATUS_COMPLETED);
                        task.setResult(Task.RESULT_FAIL);
                        task.setResultDetails("存储空间不足或读写异常，写入速度: " + writeSpeed + "MB/s");
                    } else {
                        task.setStatus(Task.STATUS_COMPLETED);
                        task.setResult(Task.RESULT_PASS);
                        task.setResultDetails("存储空间正常，可用空间: " + (freeSpace / (1024 * 1024)) + "MB，写入速度: " + writeSpeed + "MB/s");
                    }
                } catch (Exception e) {
                    task.setStatus(Task.STATUS_ERROR);
                    task.setResult(Task.RESULT_ERROR);
                    task.setResultDetails("存储测试出错: " + e.getMessage());
                }

                callback.onTaskCompleted(task);
            }
        }, 2200);
    }
}