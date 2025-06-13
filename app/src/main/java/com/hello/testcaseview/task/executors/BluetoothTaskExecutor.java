package com.hello.testcaseview.task.executors;

import com.hello.testcaseview.task.Task;
import com.hello.testcaseview.task.TaskExecutable;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;

public class BluetoothTaskExecutor implements TaskExecutable {

    private Context context;
    private Handler handler;
    
    public BluetoothTaskExecutor(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }
    
    @Override
    public void execute(final Task task, final TaskExecutable.TaskExecutionCallback callback) {
        // 模拟蓝牙测试
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 检查蓝牙状态
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                boolean hasBluetooth = (bluetoothAdapter != null);
                boolean isEnabled = hasBluetooth && bluetoothAdapter.isEnabled();
                
                if (!hasBluetooth) {
                    task.setStatus(Task.STATUS_ERROR);
                    task.setResult(Task.RESULT_ERROR);
                    task.setResultDetails("设备不支持蓝牙功能");
                } else if (!isEnabled || Math.random() < 0.2) { // 20%失败率模拟
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_FAIL);
                    task.setResultDetails("蓝牙配对失败，请检查蓝牙状态");
                } else {
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_PASS);
                    task.setResultDetails("蓝牙功能正常，可以正确配对设备");
                }
                
                // 通知执行完成
                callback.onTaskCompleted(task);
            }
        }, 2000);
    }
}