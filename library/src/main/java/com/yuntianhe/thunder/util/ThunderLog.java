package com.yuntianhe.thunder.util;

import android.util.Log;

import com.yuntianhe.thunder.Thunder;

/**
 * desc:
 * author: daiwj on 2019/4/19 20:38
 */
public class ThunderLog {

    private static boolean sEnableLog;

    public static void setEnableLog(boolean enableLog) {
        sEnableLog = enableLog;
    }

    public static final void d(String tag, String content) {
        if (sEnableLog) {
            Log.d(Thunder.TAG + "_" + tag, content);
        }
    }

    public static final void w(String tag, String content) {
        if (sEnableLog) {
            Log.w(Thunder.TAG + "_" + tag, content);
        }
    }

    public static final void e(String tag, String content) {
        if (sEnableLog) {
            Log.e(Thunder.TAG + "_" + tag, content);
        }
    }
}
