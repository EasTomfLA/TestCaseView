package com.hello.testcaseview.task;

public class Task {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_COMPLETED = 2;
    public static final int STATUS_ERROR = 3;

    public static final int RESULT_UNKNOWN = 0;
    public static final int RESULT_PASS = 1;
    public static final int RESULT_FAIL = 2;
    public static final int RESULT_ERROR = 3;

    private String id;
    private String name;
    private int status;
    private int result;
    private String resultDetails;

    // 默认构造函数
    public Task() {
        this.status = STATUS_PENDING;
        this.result = RESULT_UNKNOWN;
        this.resultDetails = "";
    }
    
    // 带参数构造函数
    public Task(String id, String name) {
        this.id = id;
        this.name = name;
        this.status = STATUS_PENDING;
        this.result = RESULT_UNKNOWN;
        this.resultDetails = "";
    }
    
    // 完整参数构造函数
    public Task(String id, String name, int status, int result, String resultDetails) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.result = result;
        this.resultDetails = resultDetails;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResultDetails() {
        return resultDetails;
    }

    public void setResultDetails(String resultDetails) {
        this.resultDetails = resultDetails;
    }
    
    // 便捷方法，判断任务是否完成
    public boolean isCompleted() {
        return status == STATUS_COMPLETED || status == STATUS_ERROR;
    }
    
    // 便捷方法，判断任务是否通过
    public boolean isPassed() {
        return result == RESULT_PASS;
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", result=" + result +
                '}';
    }
}