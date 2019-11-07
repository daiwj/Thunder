package com.yuntianhe.thunder.core;


import com.yuntianhe.thunder.util.ThunderLog;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * desc:
 * author: daiwj on 2019/4/19 16:46
 */
public class ExecutorPool {

    public static final String TAG = ExecutorPool.class.getSimpleName();

    /**
     * 可动态调整线程数的线程池
     */
    private ArrayList<TaskExecutor> mTaskExecutors;

    private TaskQueue mTaskQueue;


    /**
     * @param coreExecutorSize 最小线程数量
     * @param maxExecutorSize  最大线程数量
     * @param taskQueue
     */
    public ExecutorPool(int coreExecutorSize, int maxExecutorSize, TaskQueue taskQueue) {
        mTaskExecutors = new ArrayList<>(maxExecutorSize);
        mTaskQueue = taskQueue;

        for (int i = 0; i < coreExecutorSize; i++) {
            TaskExecutor executor = new TaskExecutor(this, mTaskQueue);
            executor.setIndex(mTaskExecutors.size());
            executor.setCoreExecutor(true);
            executor.startWaitingTask();
            mTaskExecutors.add(executor);
        }
    }

    public TaskExecutor createExecutor(boolean coreExecutor) {
        TaskExecutor executor = new TaskExecutor(this, mTaskQueue);
        executor.setIndex(mTaskExecutors.size());
        executor.setCoreExecutor(coreExecutor);
        mTaskExecutors.add(executor);
        ThunderLog.d(TAG, "create executor: " + executor.getName() + ", coreExecutor: " + coreExecutor);
        return executor;
    }

    public TaskExecutor destroyExecutor(TaskExecutor executor) {
        mTaskExecutors.remove(executor);
        ThunderLog.d(TAG, "destroy executor:  " + executor.getName() + ", coreExecutor: " + executor.isCoreExecutor());
        return executor;
    }

    public int size() {
        return mTaskExecutors.size();
    }

}
