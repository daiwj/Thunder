package com.yuntianhe.thunder.core;

import android.text.TextUtils;

import com.yuntianhe.thunder.task.Task;

/**
 * desc:
 * author: daiwj on 2019-10-29 15:26
 */
public class Dispatcher {

    private int mCoreExecutorSize;
    private int mMaxExecutorSize;

    private final TaskQueue mTaskQueue;
    private final ExecutorPool mExecutorPool;

    public Dispatcher(int coreExecutorSize, int maxExecutorSize) {
        if (coreExecutorSize > 0 && coreExecutorSize <= maxExecutorSize) {
            mCoreExecutorSize = coreExecutorSize;
        }
        if (maxExecutorSize > 0) {
            mMaxExecutorSize = maxExecutorSize;
        }
        mTaskQueue = new TaskQueue();
        mExecutorPool = new ExecutorPool(mCoreExecutorSize, mMaxExecutorSize, mTaskQueue);
    }

    public void start(Task task) {
        final int runningTaskSize = mTaskQueue.getRunningTaskSize();
        final int waitingTaskSize = mTaskQueue.getWaitingTaskSize();
        final int runningExecutorSize = mExecutorPool.size();

        if (runningTaskSize + waitingTaskSize < runningExecutorSize) {
            task.waiting();
            mTaskQueue.addWaitingTask(task);
        } else {
            if (runningExecutorSize < mCoreExecutorSize) {
                TaskExecutor executor = mExecutorPool.createExecutor(true);
                executor.startTask(task);
            } else if (runningExecutorSize < mMaxExecutorSize) {
                TaskExecutor executor = mExecutorPool.createExecutor(false);
                executor.startWaitingTask();
                task.waiting();
                mTaskQueue.addWaitingTask(task);
            } else {
                task.waiting();
                mTaskQueue.addWaitingTask(task);
            }
        }
    }

    public void pause(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }

        Task task;

        task = mTaskQueue.findRunningTask(taskId);
        if (task != null) {
            task.pause();
            mTaskQueue.removeRunningTask(task);
            mTaskQueue.addCacheTask(task);
            return;
        }

        task = mTaskQueue.findWaitingTask(taskId);
        if (task != null) {
            task.pause();
            mTaskQueue.removeWaitingTask(task);
            mTaskQueue.addCacheTask(task);
        }
    }

    public void resume(String taskId) {
        Task task = mTaskQueue.findCacheTask(taskId);
        resume(task);
    }

    public void resume(Task task) {
        if (task != null) {
            mTaskQueue.removeCacheTask(task);
            start(task);
        }
    }

    public void cancel(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }

        Task task;

        task = mTaskQueue.findRunningTask(taskId);
        if (task != null) {
            task.cancel();
            mTaskQueue.removeRunningTask(task);
            return;
        }

        task = mTaskQueue.findCacheTask(taskId);
        if (task != null) {
            task.cancel();
            mTaskQueue.removeCacheTask(task);
            return;
        }

        task = mTaskQueue.findWaitingTask(taskId);
        if (task != null) {
            task.cancel();
            mTaskQueue.removeWaitingTask(task);
        }
    }

    public TaskQueue taskQueue() {
        return mTaskQueue;
    }
}
