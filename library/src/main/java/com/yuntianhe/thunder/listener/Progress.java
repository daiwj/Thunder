package com.yuntianhe.thunder.listener;

/**
 * desc:
 * author: daiwj on 2019-10-29 15:18
 */
public class Progress implements Runnable {

    /**
     * byte length
     */
    private long mTotalLength;

    /**
     * byte length
     */
    private long mCurrentLength;

    /**
     * byte length
     */
    private long mLastLength;


    /**
     * byte
     */
    private long speed;

    public long getTotalLength() {
        return mTotalLength;
    }

    public void setTotalLength(long totalLength) {
        mTotalLength = totalLength;
    }

    public synchronized long getCurrentLength() {
        return mCurrentLength;
    }

    public void setCurrentLength(long currentLength) {
        mCurrentLength = currentLength;
    }

    public long getLastLength() {
        return mLastLength;
    }

    public void setLastLength(long lastLength) {
        mLastLength = lastLength;
    }

    public long getSpeed() {
        return mCurrentLength - mLastLength;
    }

    @Override
    public void run() {
    }
}
