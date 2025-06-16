package com.hello.testcaseview.task;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.hello.testcaseview.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView taskStatus;
        ImageView statusIcon;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            statusIcon = itemView.findViewById(R.id.statusIcon);
        }

        void bind(Task task) {
            taskName.setText(task.getName());

            // 设置状态文本
            String statusText;
            int statusColor;
            int iconRes;

            switch (task.getStatus()) {
                case Task.STATUS_PENDING:
                    statusText = "等待执行";
                    statusColor = 0xFF808080; // 灰色
                    iconRes = R.drawable.ic_pending;
                    break;
                case Task.STATUS_RUNNING:
                    statusText = "执行中...";
                    statusColor = 0xFF3F51B5; // 蓝色
                    iconRes = R.drawable.ic_running;
                    break;
                case Task.STATUS_COMPLETED:
                    statusText = "执行完成";
                    statusColor = 0xFF4CAF50; // 绿色
                    iconRes = R.drawable.ic_completed;
                    break;
                case Task.STATUS_ERROR:
                    statusText = "执行异常";
                    statusColor = 0xFFFF5722; // 橙色
                    iconRes = R.drawable.ic_error;
                    break;
                default:
                    statusText = "未知状态";
                    statusColor = 0xFF000000; // 黑色
                    iconRes = R.drawable.ic_unknown;
            }

            taskStatus.setText(statusText);
            taskStatus.setTextColor(statusColor);
            statusIcon.setImageResource(iconRes);
        }
    }
}