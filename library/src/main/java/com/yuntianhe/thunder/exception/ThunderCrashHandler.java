package com.yuntianhe.thunder.exception;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.yuntianhe.thunder.util.ThunderLog;

/**
 * desc:
 * author: daiwj on 2019-11-07 14:05
 */
public class ThunderCrashHandler implements Thread.UncaughtExceptionHandler {

    private Application mApplication;

    private Thread.UncaughtExceptionHandler mDefault;

    public ThunderCrashHandler(Application application) {
        mApplication = application;
        mDefault = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
//        mDefault.uncaughtException(t, e);
        ThunderLog.e("ThunderCrashHandler", getCrashReport(e));
    }

    private String getCrashReport(Throwable ex) {
        StringBuffer exceptionStr = new StringBuffer();
        try {
            PackageInfo packageInfo = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), 0);
            if (packageInfo != null) {
                if (ex != null) {
                    //app版本信息
                    exceptionStr.append("App Version：" + packageInfo.versionName);
                    exceptionStr.append("_" + packageInfo.versionCode + "\n");

                    //手机系统信息
                    exceptionStr.append("OS Version：" + Build.VERSION.RELEASE);
                    exceptionStr.append("_");
                    exceptionStr.append(Build.VERSION.SDK_INT + "\n");

                    //手机制造商
                    exceptionStr.append("Vendor: " + Build.MANUFACTURER + "\n");

                    //手机型号
                    exceptionStr.append("Model: " + Build.MODEL + "\n");

                    String errorStr = ex.getLocalizedMessage();
                    if (TextUtils.isEmpty(errorStr)) {
                        errorStr = ex.getMessage();
                    }
                    if (TextUtils.isEmpty(errorStr)) {
                        errorStr = ex.toString();
                    }
                    exceptionStr.append("Exception: " + errorStr + "\n");
                    StackTraceElement[] elements = ex.getStackTrace();
                    if (elements != null) {
                        for (int i = 0; i < elements.length; i++) {
                            exceptionStr.append(elements[i].toString() + "\n");
                        }
                    }
                } else {
                    exceptionStr.append("no exception. Throwable is null\n");
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return exceptionStr.toString();
    }

}
