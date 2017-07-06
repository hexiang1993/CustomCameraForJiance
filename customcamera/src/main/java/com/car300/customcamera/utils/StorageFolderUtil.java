package com.car300.customcamera.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by xhe on 2017/4/11.
 * 文件的存储路径管理
 */

public class StorageFolderUtil {
    private static final String root = "jiance/files";
    public static final String HTML_FOLDER_NAME = "html_che300_jiance";

    /**
     * 照片目录
     *
     * @param context
     * @return
     */
    public static File getPhotoFolder(Context context) {
        File photoFolder = context.getExternalFilesDir("photos");
        if (photoFolder == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + root + "/photos";
                File file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                return new File(path);
            }
        }
        return photoFolder;
    }

    /**
     * html
     * @param context
     * @return
     */
    public static File getHtmlFolder(Context context){
        File photoFolder = context.getExternalFilesDir(HTML_FOLDER_NAME);
        if (photoFolder == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + root + "/"+HTML_FOLDER_NAME;
                File file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                return new File(path);
            }
        }
        return photoFolder;
    }

    /**
     * 缓存文件跟目录
     *
     * @param context
     * @return
     */
    public static File getFileRootFolder(Context context) {
        File photoFolder = context.getExternalFilesDir(null);
        if (photoFolder == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + root;
                File file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                return new File(path);
            }
        }
        return photoFolder;
    }
}
