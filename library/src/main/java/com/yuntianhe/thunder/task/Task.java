package com.yuntianhe.thunder.task;

import android.text.TextUtils;

import com.yuntianhe.thunder.exception.TaskRunningException;
import com.yuntianhe.thunder.listener.TaskStateListener;
import com.yuntianhe.thunder.listener.TaskStateNotifier;
import com.yuntianhe.thunder.util.ThunderLog;

/**
 * desc:
 * author: daiwj on 2019-10-29 16:01
 */
public abstract class Task<T extends TaskInfo> {

    public static final String TAG = Task.class.getSimpleName();

    public T mTaskInfo;

    private String mTaskId;

    private int mTaskState = TaskState.STATE_NONE;

    public TaskStateNotifier mTaskHandler;

    private boolean mPause = false;
    private boolean mCancel = false;

    public Task(T taskInfo) {
        mTaskInfo = taskInfo;
        mTaskHandler = new TaskStateNotifier(taskInfo);

        final String taskId = onCreateTaskId();
        mTaskInfo.setTaskId(taskId);

        setTaskId(taskId);
    }

    public T getTaskInfo() {
        return mTaskInfo;
    }

    public int getTaskState() {
        return mTaskState;
    }

    public void setTaskState(@TaskState int taskState) {
        mTaskState = taskState;
    }

    public String getTaskId() {
        return mTaskId;
    }

    protected void setTaskId(String taskId) {
        this.mTaskId = taskId;
    }

    protected abstract String onCreateTaskId();

    public TaskStateNotifier getTaskHandler() {
        return mTaskHandler;
    }

    public void setTaskStateListener(TaskStateListener taskStateListener) {
        if (taskStateListener != null) {
            taskStateListener.setTaskId(mTaskInfo.getTaskId());
            taskStateListener.setTaskName(mTaskInfo.getTaskName());
            mTaskHandler.setTaskStateListener(taskStateListener);
            ThunderLog.e(TAG, mTaskInfo.getTaskName() + " set task listener!");
        }
    }

    public void clearTaskListener() {
        mTaskHandler.setTaskStateListener(null);
        ThunderLog.e(TAG, mTaskInfo.getTaskName() + " clear task listener!");
    }

    public void addTaskStateListener(TaskStateListener taskStateListener) {
        if (taskStateListener != null) {
            mTaskHandler.addTaskStateListener(taskStateListener);
        }
    }

    public void removeTaskStateListener(TaskStateListener taskStateListener) {
        mTaskHandler.removeTaskStateListener(taskStateListener);
    }

    public void waiting() {
        updateTaskState(TaskState.STATE_WAITING);
        mPause = false;
        mCancel = false;
        getTaskHandler().notifyWaiting();
    }

    public void start() throws TaskRunningException {
        updateTaskState(TaskState.STATE_START);
        mPause = false;
        mCancel = false;
    }

    public boolean isStart() {
        return mTaskState == TaskState.STATE_START
                || mTaskState == TaskState.STATE_PROGRESS;
    }

    public void pause() {
        updateTaskState(TaskState.STATE_PAUSE);
        mPause = true;
    }

    public boolean isPause() {
        return mPause || mTaskState == TaskState.STATE_PAUSE;
    }

    public void cancel() {
        updateTaskState(TaskState.STATE_CANCEL);
        mCancel = true;
    }

    public boolean isCancel() {
        return mCancel || mTaskState == TaskState.STATE_CANCEL;
    }

    public void error() {
        updateTaskState(TaskState.STATE_ERROR);
        mPause = false;
        mCancel = false;
    }

    protected void updateTaskState(int taskState) {
        setTaskState(taskState);
        mTaskInfo.setTaskState(taskState);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final TaskInfo info = getTaskInfo();
        if (!TextUtils.isEmpty(info.getTaskName())) {
            sb.append("(");
            sb.append("taskName: " + info.getTaskName() + ", taskId: " + info.getTaskId());
            sb.append(")");
        }
        return sb.toString();
    }
}
