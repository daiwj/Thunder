package com.yuntianhe.thunder.download;

import com.yuntianhe.thunder.exception.TaskRunningException;
import com.yuntianhe.thunder.task.Task;
import com.yuntianhe.thunder.util.MD5Util;
import com.yuntianhe.thunder.util.ThunderLog;

/**
 * desc:
 * author: daiwj on 2019-10-30 11:33
 */
public class DownLoadTask<T extends DownloadInfo> extends Task<T> {

    public DownLoadTask(T taskInfo) {
        super(taskInfo);
    }

    public boolean isEnableRange() {
        return getTaskInfo().isEnableRange();
    }

    @Override
    public String onCreateTaskId() {
        return MD5Util.md5(mTaskInfo.getUrl() + mTaskInfo.getFilePath());
    }

    @Override
    public final void start() throws TaskRunningException {
        boolean isStop = isPause() || isCancel();
        if (!isStop) {
            super.start();
            startDownload();
        } else {
            ThunderLog.e(TAG, "found the stopping task to start!");
        }
    }

    public void startDownload() throws TaskRunningException {
    }
}
