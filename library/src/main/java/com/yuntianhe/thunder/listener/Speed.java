package com.yuntianhe.thunder.listener;

/**
 * desc:
 * author: daiwj on 2019-11-07 11:06
 */
public class Speed implements Runnable {

    private boolean refreshSpeed = true;

    private long lastLength;

    public boolean isRefreshSpeed() {
        return refreshSpeed;
    }

    public void setRefreshSpeed(boolean refreshSpeed) {
        this.refreshSpeed = refreshSpeed;
    }

    public long getLastLength() {
        return lastLength;
    }

    public void setLastLength(long lastLength) {
        this.lastLength = lastLength;
    }

    @Override
    public void run() {
    }
}