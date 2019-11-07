package com.yuntianhe.thunder.upload;

import com.yuntianhe.thunder.task.Task;
import com.yuntianhe.thunder.task.TaskInfo;

/**
 * desc:
 * author: daiwj on 2019-11-05 20:26
 */
public class UploadInfo extends TaskInfo {

    private String url;
    private String extra;

    private boolean enableRange;

    public boolean isEnableRange() {
        return enableRange;
    }

    public void setEnableRange(boolean enableRange) {
        this.enableRange = enableRange;
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

    @Override
    public Task onCreateTask() {
        return new OkHttpUploadTask(this);
    }
}
