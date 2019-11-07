package com.yuntianhe.thunder;

import android.text.TextUtils;

import com.yuntianhe.thunder.core.Dispatcher;
import com.yuntianhe.thunder.core.TaskQueue;
import com.yuntianhe.thunder.listener.TaskStateListener;
import com.yuntianhe.thunder.task.Task;
import com.yuntianhe.thunder.task.TaskInfo;
import com.yuntianhe.thunder.util.ThunderLog;

import java.util.List;

/**
 * desc:
 * author: daiwj on 2019/4/15 14:32
 */
public class Thunder {

    public static final String TAG = Thunder.class.getSimpleName();

    private Dispatcher mDispatcher;

    private long mConnectTimeout = 60 * 60 * 1000; // 全局-连接超时时间
    private long mReadTimeout = 60 * 60 * 1000; // 全局-读超时时间
    private long mWriteTimeout = 60 * 60 * 1000; // 全局-写超时时间
    private int mCoreThreadSize = 1; // 全局-核心线程数
    private int mMaxThreadSize = 3; // 全局-最大线程数

    public Thunder() {
        mDispatcher = new Dispatcher(mCoreThreadSize, mMaxThreadSize);
    }

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        mConnectTimeout = connectTimeout;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        mReadTimeout = readTimeout;
    }

    public int getCoreThreadSize() {
        return mCoreThreadSize;
    }

    public void setCoreThreadSize(int coreThreadSize) {
        mCoreThreadSize = coreThreadSize;
    }

    public long getWriteTimeout() {
        return mWriteTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        mWriteTimeout = writeTimeout;
    }

    public int getMaxThreadSize() {
        return mMaxThreadSize;
    }

    public void setMaxThreadSize(int maxThreadSize) {
        mMaxThreadSize = maxThreadSize;
    }

    public static void setLogEnable(boolean enableLog) {
        ThunderLog.setEnableLog(enableLog);
    }

    /**
     * 开启任务
     *
     * @param list
     */
    public final void startAll(List<TaskInfo> list) {
        for (TaskInfo info : list) {
            start(info, null);
        }
    }

    /**
     * 开启任务
     *
     * @param info
     * @param listener
     */
    public final void start(TaskInfo info, final TaskStateListener listener) {
        Task task = info.onCreateTask();
        Task runningTask = mDispatcher.taskQueue().findRunningTask(info.getTaskId());
        if (runningTask != null) {
            return;
        }
        Task cacheTask = mDispatcher.taskQueue().findCacheTask(info.getTaskId());
        if (cacheTask != null) {
            cacheTask.setTaskStateListener(listener);
            mDispatcher.resume(cacheTask);
        } else {
            task.setTaskStateListener(listener);
            mDispatcher.start(task);
        }
    }

    /**
     * 暂停某个任务
     *
     * @param info
     */
    public final void pause(TaskInfo info) {
        String taskId = info.getTaskId();
        mDispatcher.pause(taskId);
    }

    /**
     * 暂停一组任务
     *
     * @param list
     */
    public final void pauseAll(List<TaskInfo> list) {
        for (TaskInfo info : list) {
            pause(info);
        }
    }

    /**
     * 根据tag恢复某个任务
     *
     * @param info
     */
    public final void resume(TaskInfo info) {
        String taskId = info.getTaskId();
        mDispatcher.resume(taskId);
    }

    /**
     * 根据tag恢复一组任务
     *
     * @param list
     */
    public final void resume(List<TaskInfo> list) {
        for (TaskInfo info : list) {
            resume(info);
        }
    }

    /**
     * 根据tag取消某个任务
     *
     * @param info
     */
    public final void cancel(TaskInfo info) {
//        mDispatcher.taskQueue().setBusy(true);
        String taskId = info.getTaskId();
        mDispatcher.cancel(taskId);
    }

    /**
     * 根据tag取消一组任务
     *
     * @param list
     */
    public final void cancel(List<TaskInfo> list) {
        for (TaskInfo info : list) {
            cancel(info);
        }
    }

    public final void setTaskStateListener(String taskId, TaskStateListener listener) {
        TaskQueue queue = mDispatcher.taskQueue();
        String oldTaskId = listener.getTaskId();
        if (!TextUtils.equals(oldTaskId, taskId)) {
            Task oldTask = queue.findTask(oldTaskId);
            if (oldTask != null) {
                oldTask.clearTaskListener();
            }
        }
        Task task = queue.findTask(taskId);
        if (task != null) {
            listener.setTaskId(taskId);
            task.setTaskStateListener(listener);
        }
    }

    public final void addTaskStateListener(String taskId, TaskStateListener listener) {
        TaskQueue queue = mDispatcher.taskQueue();
        Task task = queue.findTask(taskId);
        if (task != null) {
            String oldTaskId = listener.getTaskId();
            if (!TextUtils.isEmpty(oldTaskId)) {
                Task oldTask = queue.findTask(oldTaskId);
                if (oldTask != null) {
                    oldTask.clearTaskListener();
                }
            }
            listener.setTaskId(taskId);
            task.addTaskStateListener(listener);
        }
    }

    public final void removeTaskStateListener(String taskId, TaskStateListener listener) {
        TaskQueue queue = mDispatcher.taskQueue();
        Task task = queue.findTask(taskId);
        if (task != null) {
            listener.setTaskId(taskId);
            task.removeTaskStateListener(listener);
        }
    }
}
