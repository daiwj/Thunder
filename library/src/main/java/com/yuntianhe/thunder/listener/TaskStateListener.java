package com.yuntianhe.thunder.listener;

import com.yuntianhe.thunder.task.TaskState;

/**
 * desc:
 * author: daiwj on 2019/4/15 14:33
 */
public class TaskStateListener {

    private String taskId;

    private String taskName;

    public String getTaskId() {
        return taskId == null ? "" : taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName == null ? "" : taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @TaskState
    public void onWaiting() {

    }

    @TaskState
    public void onStart() {

    }

    @TaskState
    public void onProgress(long total, long current, long speed) {

    }

    @TaskState
    public void onSpeed(long speed) {

    }

    @TaskState
    public void onComplete(String filePath) {

    }

    @TaskState
    public void onPause() {

    }

    @TaskState
    public void onResume() {

    }

    @TaskState
    public void onCancel() {

    }

    @TaskState
    public void onError(String error) {

    }
}
