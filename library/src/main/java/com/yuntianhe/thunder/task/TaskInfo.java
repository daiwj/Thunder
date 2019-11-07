package com.yuntianhe.thunder.task;

import com.yuntianhe.thunder.listener.TaskStateMissed;

/**
 * desc:
 * author: daiwj on 2019-11-01 14:37
 */
public abstract class TaskInfo {

    private String mTaskId;
    private String mTaskName;

    private long total;
    private long current;
    private long speed;

    private int mTaskState = TaskState.STATE_NONE;

    private TaskStateMissed mTaskStateMissed;

    public String getTaskId() {
        return mTaskId == null ? "" : mTaskId;
    }

    public void setTaskId(String taskId) {
        this.mTaskId = taskId;
    }

    public String getTaskName() {
        return mTaskName == null ? "" : mTaskName;
    }

    public void setTaskName(String taskName) {
        this.mTaskName = taskName;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getTaskState() {
        return mTaskState;
    }

    public void setTaskState(int taskState) {
        mTaskState = taskState;
    }

    public TaskStateMissed getTaskStateMissed() {
        return mTaskStateMissed;
    }

    public void setTaskStateMissed(TaskStateMissed taskStateMissed) {
        mTaskStateMissed = taskStateMissed;
    }

    public boolean isStart() {
        return mTaskState == TaskState.STATE_START
                || mTaskState == TaskState.STATE_PROGRESS;
    }

    public abstract Task onCreateTask();

}
