package com.hello.testcaseview.task;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hello.testcaseview.R;

import java.util.List;

public class ResultActivity extends AppCompatActivity {
    private RecyclerView resultListView;
    private Button backButton;
    private ResultAdapter resultAdapter;
    private List<Task> taskResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 初始化控件
        resultListView = findViewById(R.id.resultListView);
        backButton = findViewById(R.id.backButton);

        // 加载测试结果
        TaskManager taskManager = new TaskManager(this);
        taskResults = taskManager.loadTaskResults();

        // 设置RecyclerView
        resultAdapter = new ResultAdapter(taskResults);
        resultListView.setLayoutManager(new LinearLayoutManager(this));
        resultListView.setAdapter(resultAdapter);

        // 设置返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 结果适配器
    private class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
        private List<Task> resultList;

        public ResultAdapter(List<Task> resultList) {
            this.resultList = resultList;
        }

        @Override
        public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_result, parent, false);
            return new ResultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ResultViewHolder holder, int position) {
            Task task = resultList.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return resultList.size();
        }

        class ResultViewHolder extends RecyclerView.ViewHolder {
            TextView taskName;
            TextView resultText;
            TextView detailsText;
            ImageView resultIcon;

            public ResultViewHolder(View itemView) {
                super(itemView);
                taskName = itemView.findViewById(R.id.resultTaskName);
                resultText = itemView.findViewById(R.id.resultText);
                detailsText = itemView.findViewById(R.id.resultDetails);
                resultIcon = itemView.findViewById(R.id.resultIcon);
            }

            void bind(Task task) {
                taskName.setText(task.getName());
                detailsText.setText(task.getResultDetails());

                // 设置结果文本和图标
                String result;
                int resultColor;
                int iconRes;

                switch (task.getResult()) {
                    case Task.RESULT_PASS:
                        result = "通过";
                        resultColor = 0xFF4CAF50; // 绿色
                        iconRes = R.drawable.ic_check;
                        break;
                    case Task.RESULT_FAIL:
                        result = "不通过";
                        resultColor = 0xFFE91E63; // 粉红色
                        iconRes = R.drawable.ic_fail;
                        break;
                    case Task.RESULT_ERROR:
                        result = "异常";
                        resultColor = 0xFFFF5722; // 橙色
                        iconRes = R.drawable.ic_warning;
                        break;
                    default:
                        result = "未知";
                        resultColor = 0xFF9E9E9E; // 灰色
                        iconRes = R.drawable.ic_unknown;
                }

                resultText.setText(result);
                resultText.setTextColor(resultColor);
                resultIcon.setImageResource(iconRes);
            }
        }
    }
}