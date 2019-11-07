package com.yuntianhe.thunder.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;

/**
 * desc:
 * author: daiwj on 2019-11-05 09:44
 */
public class ThunderFileUtil {

    public static final String DIR = "1Thunder";

    public static final String ROOT_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DIR;

    public static String makeFilePath(Context context, String fileName) {
        String root;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            root = ROOT_SDCARD;
        } else {
            root = context.getFilesDir().getAbsolutePath() + File.separator + DIR;
        }
        File dir = new File(root);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath() + File.separator + fileName;
    }

    public static String renameFile(String filePath) {
        StringBuilder sb = new StringBuilder(filePath);
        final int count = ThunderFileUtil.getSameFileCount(filePath);
        if (count > 0) {
            final String fileIndex = "(" + count + ")";
            int index = sb.lastIndexOf(".");
            sb.insert(index, fileIndex);
        }
        return sb.toString();
    }

    public static String getFileNameWithSuffix(String url) {
        final int lastSep = url.lastIndexOf(File.separator);
        return lastSep == -1 ? url : url.substring(lastSep + 1);
    }

    public static String getFileName(String fileName) {
        final int lastSep = fileName.lastIndexOf(File.separator);
        final int lastPoint = fileName.lastIndexOf(".");
        final String result = lastSep == -1 ? fileName : fileName.substring(lastSep + 1, lastPoint);
        return result;
    }

    public static int getSameFileCount(final String filePath) {
        File self = new File(filePath);
        if (self.exists()) {
            final String fileName = getFileName(filePath);
            File parent = new File(self.getParent());
            File[] fileList = parent.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String fileNameWithSuffix = pathname.getName();
                    return !TextUtils.isEmpty(fileNameWithSuffix) && fileNameWithSuffix.startsWith(fileName);
                }
            });
            return fileList.length;

        }
        return 0;
    }

}
