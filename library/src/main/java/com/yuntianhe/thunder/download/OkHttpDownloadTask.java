package com.yuntianhe.thunder.download;

import com.yuntianhe.thunder.exception.DownloadInterruptException;
import com.yuntianhe.thunder.exception.TaskConnectException;
import com.yuntianhe.thunder.exception.TaskRunningException;
import com.yuntianhe.thunder.task.TaskState;
import com.yuntianhe.thunder.util.ThunderFileUtil;
import com.yuntianhe.thunder.util.ThunderLog;

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
public class OkHttpDownloadTask extends DownLoadTask<DownloadInfo> {

    public static final String TAG = OkHttpDownloadTask.class.getSimpleName();

    private static final OkHttpClient sOkHttpClient = new OkHttpClient();

    private Call mCall;

    public OkHttpDownloadTask(DownloadInfo taskInfo) {
        super(taskInfo);
    }

    @Override
    public void startDownload() throws TaskRunningException {
        mTaskHandler.notifyStart();

        try {
            String filePath = mTaskInfo.getFilePath();
            if (new File(filePath).exists() && mTaskInfo.isEnableRename()) {
                filePath = ThunderFileUtil.renameFile(filePath);
                mTaskInfo.setFilePath(filePath);
            }
            final File tempFile = new File(filePath + ".temp");
            if (tempFile.exists()) {
                if (!isEnableRange()) {
                    tempFile.delete();
                    tempFile.createNewFile();
                }
            } else {
                tempFile.createNewFile();
            }
            connect(tempFile);
        } catch (Exception e) {
            ThunderLog.e(TAG, e.getMessage());
            error();
            throw new TaskRunningException(e.getMessage());
        }
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
                download(tempFile, response.body());
            } else {
                throw new TaskConnectException(toString() + response.message());
            }
        } catch (IOException e) {
            throw new TaskConnectException(toString() + " connect failed!");
        } catch (DownloadInterruptException e) {
            throw e;
        }
    }

    /**
     * start download and call back the progress if need
     *
     * @param file
     * @throws IOException
     */
    private void download(File file, final ResponseBody body) throws DownloadInterruptException {
        final long range = file.length();

        InputStream is = body.byteStream();
        BufferedInputStream bis;
        RandomAccessFile randomAccessFile;

        final long localLength = file.length();
        final long totalLength = localLength + body.contentLength();

        int length;
        byte[] buff = new byte[256 * 1024]; // 1MB

        try {

            bis = new BufferedInputStream(is);
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(range);

            long totalBytesRead = localLength;

            boolean isStop = isPause() || isCancel();

            if (!isStop) {
                updateTaskState(TaskState.STATE_PROGRESS);
            }

            while (!isStop && (length = bis.read(buff)) != -1) {
                randomAccessFile.write(buff, 0, length);
                totalBytesRead += length != -1 ? length : 0;
//                int progress = (int) (totalBytesRead * 1.0f / totalLength * 100);
//                ThunderLog.w(TAG, mTaskInfo.getTaskName() + " progress: " + progress);
                mTaskHandler.notifyProgress(totalLength, totalBytesRead);
            }

            try {
                if (is != null) is.close();
                if (bis != null) bis.close();
                if (randomAccessFile != null) randomAccessFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!isStop) {
                final String filePath = mTaskInfo.getFilePath();
                file.renameTo(new File(filePath));
                updateTaskState(TaskState.STATE_COMPLETE);
                mTaskHandler.notifyComplete(filePath);
            }
        } catch (Exception e) {
            if (isPause()) {
                updateTaskState(TaskState.STATE_PAUSE);
                mTaskHandler.notifyPause();
            } else if (isCancel()) {
                deleteTempFile();
                updateTaskState(TaskState.STATE_CANCEL);
                mTaskHandler.notifyCancel();
            } else {
                throw new DownloadInterruptException(toString() + " download interrupted!");
            }
        }
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
        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
            mCall = null;
        } else {
            deleteTempFile();
            mTaskHandler.notifyCancel();
        }
    }

    @Override
    public void error() {
        super.error();
        if (!isEnableRange()) {
            deleteTempFile();
        }
        mTaskHandler.notifyError(toString() + " download failed!");
    }

    private void deleteTempFile() {
        final File tempFile = new File(mTaskInfo.getFilePath() + ".temp");
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }
}
