package com.yuntianhe.thunder.download;

import com.yuntianhe.thunder.task.Task;
import com.yuntianhe.thunder.task.TaskInfo;

/**
 * desc:
 * author: daiwj on 2019/4/15 14:39
 */
public class DownloadInfo extends TaskInfo {

    private String url;
    private String filePath; // /x/y/abc.image
    private String fileName; // abc.image
    private String extra;

    private boolean enableRange;
    private boolean enableRename;

    public boolean isEnableRange() {
        return enableRange;
    }

    public void setEnableRange(boolean enableRange) {
        this.enableRange = enableRange;
    }

    public boolean isEnableRename() {
        return enableRename;
    }

    public void setEnableRename(boolean enableRename) {
        this.enableRename = enableRename;
    }

    public String getExtra() {
        return extra == null ? "" : extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public synchronized void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName == null ? "" : fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Task onCreateTask() {
        return new OkHttpDownloadTask(this);
    }
}
