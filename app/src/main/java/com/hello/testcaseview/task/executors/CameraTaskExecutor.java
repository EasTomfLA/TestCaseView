package com.hello.testcaseview.task.executors;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.hello.testcaseview.task.Task;
import com.hello.testcaseview.task.TaskExecutable;

public class CameraTaskExecutor implements TaskExecutable {

    private Context context;
    private Handler handler;

    public CameraTaskExecutor(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void execute(final Task task, final TaskExecutable.TaskExecutionCallback callback) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean hasCamera = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

                if (!hasCamera) {
                    task.setStatus(Task.STATUS_ERROR);
                    task.setResult(Task.RESULT_ERROR);
                    task.setResultDetails("设备没有相机功能");
                } else if (Math.random() < 0.2) { // 20%失败率模拟
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_FAIL);
                    task.setResultDetails("相机预览异常，请检查权限或重启设备");
                } else {
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_PASS);
                    task.setResultDetails("相机功能正常，拍摄与预览均可用");
                }

                callback.onTaskCompleted(task);
            }
        }, 1800);
    }
}