package com.yuntianhe.thunder.task;

/**
 * desc:
 * author: daiwj on 2019-10-30 13:54
 */
public @interface TaskState {
    int STATE_NONE = -1;
    int STATE_WAITING = 0;
    int STATE_START = 1;
    int STATE_PROGRESS = 2;
    int STATE_COMPLETE = 3;
    int STATE_RESUME = 4;
    int STATE_PAUSE = 5;
    int STATE_CANCEL = 6;
    int STATE_ERROR = 7;
}
