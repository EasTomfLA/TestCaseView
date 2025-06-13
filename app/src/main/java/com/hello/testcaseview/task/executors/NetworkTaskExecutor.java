package com.hello.testcaseview.task.executors;


import android.content.Context;
import android.os.Handler;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hello.testcaseview.task.Task;
import com.hello.testcaseview.task.TaskExecutable;

public class NetworkTaskExecutor implements TaskExecutable {

    private Context context;
    private Handler handler;
    
    public NetworkTaskExecutor(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }
    
    @Override
    public void execute(final Task task, final TaskExecutionCallback callback) {
        // 模拟网络测试
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 检查网络状态
                ConnectivityManager cm = (ConnectivityManager) 
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                
                // 随机生成延迟值模拟测试结果
                int randomDelay = 10 + (int)(Math.random() * 50);
                
                if (!isConnected || Math.random() < 0.2) { // 20%失败率模拟
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_FAIL);
                    task.setResultDetails("网络连接失败，请检查网络设置");
                } else if (randomDelay > 40) { // 延迟过高
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_FAIL);
                    task.setResultDetails("网络延迟过高: " + randomDelay + "ms，超出预期");
                } else {
                    task.setStatus(Task.STATUS_COMPLETED);
                    task.setResult(Task.RESULT_PASS);
                    task.setResultDetails("网络连接正常，平均延迟" + randomDelay + "ms");
                }
                
                // 通知执行完成
                callback.onTaskCompleted(task);
            }
        }, 1500);
    }
}