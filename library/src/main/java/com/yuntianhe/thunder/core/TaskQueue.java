package com.yuntianhe.thunder.core;

import android.text.TextUtils;

import com.yuntianhe.thunder.task.Task;
import com.yuntianhe.thunder.task.TaskState;
import com.yuntianhe.thunder.util.ThunderLog;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * desc:
 * author: daiwj on 2019/4/15 15:15
 */
public class TaskQueue {

    public static final String TAG = "Queue";

    private final LinkedList<Task> mWaitingQueue;
    private final LinkedList<Task> mRunningQueue;
    private final LinkedList<Task> mCacheQueue;

    public TaskQueue() {
        mRunningQueue = new LinkedList<>();
        mWaitingQueue = new LinkedList<>();
        mCacheQueue = new LinkedList<>();
    }

    public synchronized void addWaitingTask(Task task) {
        if (!mWaitingQueue.contains(task)) {
            final int taskState = task.getTaskState();
            if (taskState == TaskState.STATE_NONE || taskState == TaskState.STATE_ERROR) {
                mWaitingQueue.addLast(task);
            } else {
                mWaitingQueue.addFirst(task);
            }
            ThunderLog.d(TAG, "notify all!");
            notifyAll();
        }
    }

    public synchronized boolean removeWaitingTask(Task task) {
        return task != null ? mWaitingQueue.remove(task) : false;
    }

    public synchronized boolean removeWaitingTask(String taskId) {
        return removeWaitingTask(findWaitingTask(taskId));
    }

    public synchronized Task findWaitingTask(String taskId) {
        for (Task task : mWaitingQueue) {
            if (TextUtils.equals(taskId, task.getTaskId())) {
                return task;
            }
        }
        return null;
    }

    public synchronized Task takeWaitingTask() {
        if (mWaitingQueue.size() != 0) {
            Task task = mWaitingQueue.removeFirst();
            return task;
        }
        return null;
    }

    public synchronized int getWaitingTaskSize() {
        return mWaitingQueue.size();
    }

    public synchronized void addRunningTask(Task task) {
        if (!mRunningQueue.contains(task)) {
            mRunningQueue.add(task);
        }
    }

    public synchronized boolean removeRunningTask(Task task) {
        return task != null ? mRunningQueue.remove(task) : false;
    }

    public synchronized boolean removeRunningTask(String taskId) {
        return removeRunningTask(findRunningTask(taskId));
    }

    public synchronized Task findRunningTask(String taskId) {
        for (Task task : mRunningQueue) {
            if (TextUtils.equals(taskId, task.getTaskId())) {
                return task;
            }
        }
        return null;
    }

    public synchronized int getRunningTaskSize() {
        return mRunningQueue.size();
    }

    public synchronized void addCacheTask(Task task) {
        if (!mCacheQueue.contains(task)) {
            mCacheQueue.add(task);
        }
    }

    public synchronized boolean removeCacheTask(Task task) {
        return task != null ? mCacheQueue.remove(task) : false;
    }

    public synchronized boolean removeCacheTask(String taskId) {
        return removeCacheTask(findCacheTask(taskId));
    }

    public synchronized Task findCacheTask(String taskId) {
        for (Task task : mCacheQueue) {
            if (TextUtils.equals(taskId, task.getTaskId())) {
                return task;
            }
        }
        return null;
    }

    public synchronized int getCacheTaskSize() {
        return mCacheQueue.size();
    }

    public synchronized boolean isEmpty() {
        return mWaitingQueue.isEmpty();
    }

    public synchronized Task findTask(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            return null;
        }

        for (Task task : mRunningQueue) {
            if (TextUtils.equals(taskId, task.getTaskId())) {
                return task;
            }
        }
        for (Task task : mWaitingQueue) {
            if (TextUtils.equals(taskId, task.getTaskId())) {
                return task;
            }
        }
        for (Task task : mCacheQueue) {
            if (TextUtils.equals(taskId, task.getTaskId())) {
                return task;
            }
        }

        return null;
    }

    public synchronized int totalSize() {
        return getRunningTaskSize() + getCacheTaskSize() + getWaitingTaskSize();
    }
}
