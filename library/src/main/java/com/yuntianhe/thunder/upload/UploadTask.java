package com.yuntianhe.thunder.upload;

import com.yuntianhe.thunder.exception.TaskRunningException;
import com.yuntianhe.thunder.task.Task;

/**
 * desc:
 * author: daiwj on 2019-11-05 20:25
 */
public class UploadTask<T extends UploadInfo> extends Task<T> {

    public UploadTask(T taskInfo) {
        super(taskInfo);
    }

    @Override
    public String onCreateTaskId() {
        return null;
    }

    protected boolean isEnableRange() {
        return mTaskInfo.isEnableRange();
    }

    @Override
    public final void start() throws TaskRunningException {
        super.start();
        startUpload();
    }

    public void startUpload() throws TaskRunningException {
    }
}
