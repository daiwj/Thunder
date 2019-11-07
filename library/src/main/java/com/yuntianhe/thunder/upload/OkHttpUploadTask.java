package com.yuntianhe.thunder.upload;

import com.yuntianhe.thunder.download.DownloadInfo;
import com.yuntianhe.thunder.exception.DownloadInterruptException;
import com.yuntianhe.thunder.exception.TaskConnectException;
import com.yuntianhe.thunder.exception.TaskRunningException;
import com.yuntianhe.thunder.exception.UploadInterruptException;
import com.yuntianhe.thunder.task.TaskState;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * desc:
 * author: daiwj on 2019/4/17 09:58
 */
public class OkHttpUploadTask extends UploadTask<UploadInfo> {

    public static final String TAG = OkHttpUploadTask.class.getSimpleName();

    private static final OkHttpClient sOkHttpClient = new OkHttpClient();

    private Call mCall;

    public OkHttpUploadTask(UploadInfo taskInfo) {
        super(taskInfo);
    }

    @Override
    public void startUpload() throws TaskRunningException {
        mTaskHandler.notifyStart();
    }

    private void connect(File tempFile) throws TaskRunningException {
        try {
            if (mCall != null && !mCall.isCanceled()) {
                mCall.cancel();
                mCall = null;
            }
            Request.Builder builder = new Request.Builder().url(mTaskInfo.getUrl());
            builder.addHeader("Connection", "Keep-Alive");
            if (isEnableRange()) {
                builder.addHeader("Range", "bytes=" + tempFile.length() + "-");
            }
            Request request = builder.tag(getTaskId()).build();
            mCall = sOkHttpClient.newCall(request);
            Response response = mCall.execute();
            if (response.isSuccessful()) {
                upload(tempFile, response.body());
            } else {
                throw new TaskConnectException(toString() + response.message());
            }
        } catch (IOException e) {
            throw new TaskConnectException(toString() + " connect failed!");
        } catch (UploadInterruptException e) {
            throw e;
        }
    }

    /**
     * start upload and call back the progress if need
     *
     * @param file
     * @throws IOException
     */
    private void upload(File file, final ResponseBody body) throws UploadInterruptException {
    }

    @Override
    public void pause() {
        super.pause();
        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
            mCall = null;
        } else {
            mTaskHandler.notifyPause();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void error() {
        super.error();
    }
}
