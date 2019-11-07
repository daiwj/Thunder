package com.yuntianhe.thunder.core;

import com.yuntianhe.thunder.exception.TaskConnectException;
import com.yuntianhe.thunder.exception.TaskRunningException;
import com.yuntianhe.thunder.task.Task;
import com.yuntianhe.thunder.util.ThunderLog;

/**
 * desc:
 * author: daiwj on 2019/4/19 16:18
 */
public class TaskExecutor extends Thread {

    public static final String TAG = TaskExecutor.class.getSimpleName();

    private boolean isCoreExecutor;

    private ExecutorPool mExecutorPool;
    private TaskQueue mTaskQueue;

    /**
     * thread index
     */
    private int index;

    private long MAX_KEEP_ALIVE_TIME_MILLIS = 60 * 1000;

    private Task coreTask;

    public TaskExecutor(ExecutorPool pool, TaskQueue taskQueue) {
        mExecutorPool = pool;
        mTaskQueue = taskQueue;
    }

    @Override
    public void run() {
        long startRunningTimeMillis = System.currentTimeMillis();
        while (true) {
            Task task = coreTask;
            try {
                if (isCoreExecutor) {
                    synchronized (mTaskQueue) {
                        if (task == null && mTaskQueue.isEmpty()) {
                            ThunderLog.d(TAG, getName() + " waiting task...");
                            mTaskQueue.wait();
                        }
                    }
                }
                if (task == null) {
                    task = mTaskQueue.takeWaitingTask();
                }
                if (task != null) {
                    ThunderLog.d(TAG, getName() + " start task: " + task.getTaskInfo().getTaskName());
                    mTaskQueue.addRunningTask(task);
                    task.start();
                    coreTask = null;
                    mTaskQueue.removeRunningTask(task);
                    ThunderLog.d(TAG, getName() + " stop task: " + task.getTaskInfo().getTaskName());
                    startRunningTimeMillis = System.currentTimeMillis();
                } else {
                    if (isCoreExecutor) {
                        final long endTimeMillis = System.currentTimeMillis();
                        if (endTimeMillis - startRunningTimeMillis >= MAX_KEEP_ALIVE_TIME_MILLIS) {
                            mExecutorPool.destroyExecutor(this);
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                mExecutorPool.destroyExecutor(this);
            } catch (TaskRunningException e) {
                mTaskQueue.removeRunningTask(task);
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        setName("TaskExecutor-" + index);
    }

    public void setCoreExecutor(boolean coreExecutor) {
        isCoreExecutor = coreExecutor;
    }

    public boolean isCoreExecutor() {
        return isCoreExecutor;
    }

    public void startTask(Task task) {
        coreTask = task;
        start();
    }

    public void startWaitingTask() {
        start();
    }
}
