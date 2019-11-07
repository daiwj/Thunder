package com.yuntianhe.thunder.listener;

import android.os.Handler;
import android.os.Message;

import com.yuntianhe.thunder.task.TaskInfo;
import com.yuntianhe.thunder.task.TaskState;
import com.yuntianhe.thunder.util.ThunderLog;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:
 * author: daiwj on 2019/4/16 14:49
 */
public class TaskStateNotifier extends Handler {

    private static final String TAG = TaskStateNotifier.class.getSimpleName();

    private TaskInfo mTaskInfo;

    private TaskStateListener mTaskStateListener;

    private TaskStateMissed mTaskStateMissed;

    private List<TaskStateListener> mTaskStateListeners;

    private Progress mProgress;

    private Speed mSpeed;

    public TaskStateNotifier(TaskInfo info) {
        mTaskInfo = info;
        mTaskStateMissed = info.getTaskStateMissed();
        mTaskStateListeners = new ArrayList<>();
        mProgress = new Progress() {
            @Override
            public void run() {
                onNotifyProgress(getTotalLength(), getCurrentLength(), getSpeed());
            }
        };
        mSpeed = new Speed() {
            @Override
            public void run() {
                onNotifySpeed();
                mSpeed.setRefreshSpeed(true);
//                mProgress.setLastLength(mProgress.getCurrentLength());
//                postDelayed(this, 1000);
            }
        };
    }

    public void setTaskStateListener(TaskStateListener taskStateListener) {
        mTaskStateListener = taskStateListener;
    }

    public void addTaskStateListener(TaskStateListener listener) {
        if (!mTaskStateListeners.contains(listener)) {
            mTaskStateListeners.add(listener);
        }
    }

    public void removeTaskStateListener(TaskStateListener listener) {
        if (mTaskStateListeners.contains(listener)) {
            mTaskStateListeners.remove(listener);
        }
    }

    public void notifyWaiting() {
        Message message = obtainMessage();
        message.what = TaskState.STATE_WAITING;
        sendMessage(message);
    }

    private void onNotifyWaiting() {
        ThunderLog.d(TAG, mTaskInfo.getTaskName() + " onWaiting...");
        if (mTaskStateListener != null) {
            mTaskStateListener.onWaiting();
        }
        if (mTaskStateListener == null && mTaskStateMissed != null) {
            mTaskStateMissed.onWaiting();
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onWaiting();
        }
    }

    public void notifyStart() {
        Message message = obtainMessage();
        message.what = TaskState.STATE_START;
        sendMessage(message);
    }

    private void onNotifyStart() {
        ThunderLog.d(TAG, mTaskInfo.getTaskName() + " onStart!");

        removeCallbacks(mSpeed);
        mSpeed.setRefreshSpeed(true);

        if (mTaskStateListener != null) {
            mTaskStateListener.onStart();
        } else {
            ThunderLog.e(TAG, mTaskInfo.getTaskName() + " not found task listener!");
        }
        if (mTaskStateListener == null && mTaskStateMissed != null) {
            mTaskStateMissed.onStart();
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onStart();
        }
    }

    public void notifyProgress(final long total, final long current) {
        mTaskInfo.setTotal(total);
        mTaskInfo.setCurrent(current);
        mProgress.setTotalLength(total);
        mProgress.setCurrentLength(current);
        post(mProgress);

        if (mSpeed.isRefreshSpeed()) {
            mSpeed.setRefreshSpeed(false);
            mProgress.setLastLength(mProgress.getCurrentLength());
            postDelayed(mSpeed, 1000);
        }
//        notifySpeed();
    }

    private void onNotifyProgress(long total, long current, long speed) {
        if (mTaskStateListener != null) {
            mTaskStateListener.onProgress(total, current, speed);
        }
        if (mTaskStateListener == null && mTaskStateMissed != null) {
            mTaskStateMissed.onProgress(total, current, speed);
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onProgress(total, current, speed);
        }
    }

    public void notifySpeed() {
        if (mSpeed.isRefreshSpeed()) {
            mProgress.setLastLength(mProgress.getCurrentLength());
            postDelayed(mSpeed, 1000);
        }
    }

    private void onNotifySpeed() {
        final long speed = mProgress.getSpeed();
        mTaskInfo.setSpeed(speed);

        if (mTaskStateListener != null) {
            mTaskStateListener.onSpeed(speed);
        }
        if (mTaskStateListener == null && mTaskStateMissed != null) {
            mTaskStateMissed.onSpeed(speed);
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onSpeed(speed);
        }
    }

    public void notifyComplete(String filePath) {
        Message message = obtainMessage();
        message.what = TaskState.STATE_COMPLETE;
        message.obj = filePath;
        sendMessage(message);
    }

    private void onNotifyComplete(String filePath) {
        ThunderLog.d(TAG, mTaskInfo.getTaskName() + " onComplete!");

        if (mTaskStateListener != null) {
            mTaskStateListener.onComplete(filePath);
        }
        if (mTaskStateListener == null && mTaskStateMissed != null) {
            mTaskStateMissed.onComplete(filePath);
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onComplete(filePath);
        }
    }

    public void notifyResume() {
        Message message = obtainMessage();
        message.what = TaskState.STATE_RESUME;
        sendMessage(message);
    }

    private void onNotifyResume() {
        ThunderLog.d(TAG, mTaskInfo.getTaskName() + " onResume!");

        if (mTaskStateListener != null) {
            mTaskStateListener.onResume();
        }
        if (mTaskStateListener == null && mTaskStateMissed != null) {
            mTaskStateMissed.onResume();
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onResume();
        }
    }

    public void notifyPause() {
        Message message = obtainMessage();
        message.what = TaskState.STATE_PAUSE;
        sendMessage(message);
    }

    private void onNotifyPause() {
        ThunderLog.d(TAG, mTaskInfo.getTaskName() + " onPause!");

        removeCallbacks(mProgress);
        removeCallbacks(mSpeed);
        mTaskInfo.setSpeed(0);

        if (mTaskStateListener != null) {
            mTaskStateListener.onPause();
        }
        if (mTaskStateListener == null && mTaskStateMissed != null) {
            mTaskStateMissed.onPause();
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onPause();
        }
    }

    public void notifyCancel() {
        Message message = obtainMessage();
        message.what = TaskState.STATE_CANCEL;
        sendMessage(message);
    }

    private void onNotifyCancel() {
        ThunderLog.d(TAG, mTaskInfo.getTaskName() + " onCancel!");

        removeCallbacks(mProgress);
        removeCallbacks(mSpeed);
        mTaskInfo.setTotal(0);
        mTaskInfo.setCurrent(0);
        mTaskInfo.setSpeed(0);
        mProgress.setTotalLength(0);
        mProgress.setCurrentLength(0);

        if (mTaskStateListener != null) {
            mTaskStateListener.onCancel();
        }
        if (mTaskStateListener == null && mTaskStateMissed != null) {
            mTaskStateMissed.onCancel();
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onCancel();
        }
    }

    public void notifyError(String error) {
        Message message = obtainMessage();
        message.what = TaskState.STATE_ERROR;
        message.obj = error;
        sendMessage(message);
    }

    private void onNotifyError(String error) {
        ThunderLog.e(TAG, mTaskInfo.getTaskName() + " onError!");

        removeCallbacks(mProgress);
        removeCallbacks(mSpeed);
        mTaskInfo.setTotal(0);
        mTaskInfo.setCurrent(0);
        mTaskInfo.setSpeed(0);
        mProgress.setTotalLength(0);
        mProgress.setCurrentLength(0);

        if (mTaskStateListener != null) {
            mTaskStateListener.onError(error);
        }
        if (mTaskStateMissed != null) {
            mTaskStateMissed.onError(error);
        }
        for (TaskStateListener listener : mTaskStateListeners) {
            listener.onError(error);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        final int status = msg.what;
        switch (status) {
            case TaskState.STATE_WAITING:
                onNotifyWaiting();
                break;
            case TaskState.STATE_START:
                onNotifyStart();
                break;
//                case TaskStateListener.STATE_PROGRESS:
//                    Progress progress = (Progress) msg.obj;
//                    listener.notifyProgress(progress.getTotalLength(), progress.getCurrentLength());
//                    break;
            case TaskState.STATE_COMPLETE:
                String filePath = (String) msg.obj;
                onNotifyComplete(filePath);
                break;
            case TaskState.STATE_PAUSE:
                onNotifyPause();
                break;
            case TaskState.STATE_RESUME:
                onNotifyResume();
                break;
            case TaskState.STATE_CANCEL:
                onNotifyCancel();
                break;
            case TaskState.STATE_ERROR:
                String errorMessage = (String) msg.obj;
                onNotifyError(errorMessage);
                break;
        }
    }
}
