package com.hello.testcaseview.task.executors;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;

import com.hello.testcaseview.task.Task;
import com.hello.testcaseview.task.TaskExecutable;

public class BatteryTaskExecutor implements TaskExecutable {

    private Context context;
    private Handler handler;

    public BatteryTaskExecutor(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void execute(final Task task, final TaskExecutable.TaskExecutionCallback callback) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取电池信息
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = context.registerReceiver(null, ifilter);

                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    float batteryPct = level * 100 / (float)scale;

                    int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10;
                    int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                    if (temperature > 45 || Math.random() < 0.2) { // 电池温度过高或随机失败
                        task.setStatus(Task.STATUS_COMPLETED);
                        task.setResult(Task.RESULT_FAIL);
                        task.setResultDetails("电池温度异常 (" + temperature + "°C)，请检查电池状况");
                    } else {
                        task.setStatus(Task.STATUS_COMPLETED);
                        task.setResult(Task.RESULT_PASS);
                        String statusText = "未知";
                        switch (status) {
                            case BatteryManager.BATTERY_STATUS_CHARGING: statusText = "充电中"; break;
                            case BatteryManager.BATTERY_STATUS_DISCHARGING: statusText = "放电中"; break;
                            case BatteryManager.BATTERY_STATUS_FULL: statusText = "已充满"; break;
                            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: statusText = "未充电"; break;
                        }
                        task.setResultDetails("电池性能正常，电量: " + batteryPct + "%，温度: " + temperature + "°C，状态: " + statusText);
                    }
                } catch (Exception e) {
                    task.setStatus(Task.STATUS_ERROR);
                    task.setResult(Task.RESULT_ERROR);
                    task.setResultDetails("电池测试出错: " + e.getMessage());
                }

                callback.onTaskCompleted(task);
            }
        }, 1500);
    }
}