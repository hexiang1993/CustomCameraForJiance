package com.car300.customcamera.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * xhe
 * 文件操作类
 */
public class FileUtil {
    public static final String LOCAL_FILE_URI_PREFIX = "file://";

    public static void saveJsonToFilePrivate(String fileName, String json, Context context) {
        writeObjectToFile(fileName, json, context);
    }

    public static String getJsonFromFilePrivate(String fileName, Context context) {
        Object obj = readObjectFromFile(fileName, context);
        if (obj == null) {
            return null;
        }
        Log.d("FileUtil", "文件中获取json：" + obj.toString());
        return obj.toString();
    }

    /**
     * 判断某私有文件是否存在
     *
     * @param fileName
     * @param context
     * @return
     */
    public static boolean privateFileExists(String fileName, Context context) {
        String[] files = context.fileList();
        for (String file : files) {
            if (fileName.equals(file)) {
                return true;
            }
        }
        return false;
    }

    public static Object readObjectFromFile(String fileName, Context context) {
        FileInputStream is = null;
        ObjectInputStream ois = null;
        Object obj = null;
        try {
            String[] files = context.fileList();
            boolean exists = false;
            for (String file : files) {
                if (fileName.equals(file)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) return obj;

            is = context.openFileInput(fileName);
            ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (Exception e) {
            Log.e("FileUtil", "Can not read object file:" + fileName, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    /**
     * 写到内置存储卡
     *
     * @param fileName
     * @param content
     * @param context
     * @return
     */
    public static boolean writeObjectToFile(String fileName, Object content, Context context) {
        if (privateFileExists(fileName, context)) {//文件存在，先删除
            context.deleteFile(fileName);
        }
        boolean isSusccess = false;
        try {
            FileOutputStream os = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(content);
            os.close();
            oos.close();
            isSusccess = true;
        } catch (Exception e) {
            Log.e("FileUtil", "Can't write content to file " + fileName, e);
        }

        return isSusccess;
    }

    /**
     * 写到SD卡上，路径：fileName
     *
     * @param fileName
     * @param content
     * @return
     */
    public static boolean writeByteToSDFile(String fileName, byte[] content) {
        boolean isSusccess = false;
        try {
            File file = new File(fileName);
            //先判断文件目录是否存在，不存在则创建，防止有些手机不会去自动创建
            File entryDir = new File(file.getParent());
            if (!entryDir.exists()) {
                entryDir.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(content);
            fos.close();
            isSusccess = true;
        } catch (Exception e) {
            Log.e("FileUtil", "Can't write content to file " + fileName, e);
        }
        return isSusccess;
    }

    /**
     * 解压缩
     *
     * @param is
     * @throws IOException
     */
    public static void unzipFile(String oldPath, InputStream is, Context context) throws IOException {
        String newTempPath = oldPath + "_new";
        int BUFFER = 4096; // 这里缓冲区我们使用4KB，
        String strEntry; // 保存每个zip的条目名称

        BufferedOutputStream dest = null; // 缓冲输出流
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry; // 每个zip条目的实例
        while ((entry = zis.getNextEntry()) != null) {
            boolean isDirectory = entry.isDirectory();
            if (isDirectory)
                continue;
            int count;
            byte data[] = new byte[BUFFER];
            strEntry = entry.getName();
            String filePath = oldPath + File.separator + strEntry;
            filePath = filePath.replace(oldPath, newTempPath);
            File entryFile = new File(filePath);
            File entryDir = new File(entryFile.getParent());
            if (!entryDir.exists()) {
                entryDir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(entryFile);
            dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            try {
                dest.close();
            } catch (Exception e) {

            }
        }
        try {
            zis.close();
        } catch (Exception e) {

        }

        // delete old folder
        cleanDir(new File(oldPath));
        //rename new folder
        File folder = new File(newTempPath);
        folder.renameTo(new File(oldPath));
    }

    public static void unzipHTML(Context context,InputStream is) throws IOException {
        String tempNewFolder = StorageFolderUtil.HTML_FOLDER_NAME;
        int BUFFER = 4096; // 这里缓冲区我们使用4KB，
        String strEntry; // 保存每个zip的条目名称

        // download new HTMLs to new folder
        BufferedOutputStream dest = null; // 缓冲输出流
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry; // 每个zip条目的实例
        while ((entry = zis.getNextEntry()) != null) {
            boolean isDirectory = entry.isDirectory();
            if (isDirectory)
                continue;
            int count;
            byte data[] = new byte[BUFFER];
            strEntry = entry.getName();
            String filePath = StorageFolderUtil.getFileRootFolder(context).getAbsolutePath() + File.separator + strEntry;
            filePath = filePath.replace(StorageFolderUtil.HTML_FOLDER_NAME, tempNewFolder);
            File entryFile = new File(filePath);
            File entryDir = new File(entryFile.getParent());
            if (!entryDir.exists()) {
                entryDir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(entryFile);
            dest = new BufferedOutputStream(fos, BUFFER);
            while ((count = zis.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            try {
                dest.close();
            } catch (Exception e) {

            }
        }
        try {
            zis.close();
        } catch (Exception e) {

        }
//         delete old folder
//        cleanDir(ApplicationUtil.getHTMLFolder());
//        //rename new folder
//        File folder = new File(ApplicationUtil.getFilesFolder().getAbsolutePath() + File.separator + tempNewFolder);
//        folder.renameTo(ApplicationUtil.getHTMLFolder());
    }


    public static void cleanDir(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }
        Log.d("FileUtil", "准备清除：" + dir.getAbsolutePath());
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanDir(file);
                }
                Log.d("FileUtil", "清除：" + file.getAbsolutePath());
                file.delete();
            }
        }

        dir.delete();
    }


    public static boolean existsFile(Context context, String path) {
        if (!StringUtil.isEmpty(path)) {
            File file = new File(path);
            if (file != null && file.exists()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 文件拷贝
     */
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int copyFile(String fromFile, String toFile) {
        if (StringUtil.isEmpty(fromFile) || StringUtil.isEmpty(toFile)) {
            return -1;
        }
        try {
            File file = new File(toFile);
            File entryDir = new File(file.getParent());
            if (!entryDir.exists()) {
                entryDir.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            Log.d("FileUtil", "复制文件成功");
            return 0;

        } catch (Exception ex) {
            Log.e("FileUtil", "复制文件失败：" + ex.getLocalizedMessage());
            return -1;
        }
    }

    public static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyAndShowSystemPhoto(Context context, String fromFile, String toFile) {
        copyFile(fromFile, toFile);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.parse(toFile);
        Uri uri = Uri.fromFile(new File(toFile));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 下载文件
     *
     * @param file
     * @param urlStr
     */
    public static void downloadFile(File file, String urlStr) {
        //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
        if (file.exists()) {
            file.delete();
        }
        HttpURLConnection con = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            URL url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            int contentLength = con.getContentLength();
            is = con.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            os = new FileOutputStream(file);
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }

    /**
     * 删除文件或者文件夹下所有文件
     *
     * @param path
     */
    public static void deleteFile(String path) {
        if (path == null || path.trim().length() <= 0) {
            return;
        }
        File file = new File(path.replace(LOCAL_FILE_URI_PREFIX, ""));
        if (file.exists()) {
            file.delete();
            if(file.isFile()){
                file.delete();
                return;
            }
            if(file.isDirectory()){
                File[] childFile = file.listFiles();
                if(childFile == null || childFile.length == 0){
                    file.delete();
                    return;
                }
                for(File f : childFile){
                    deleteFile(f.getAbsolutePath());
                }
                file.delete();
            }
        }
    }

    /**
     * 根据路径获取文件名+后缀
     * 123.jpg
     * @param path
     * @return
     */
    public static String getFileNameFromUrl(String path) {
        if (StringUtil.isEmpty(path)) {
            return "";
        }
        int start = path.lastIndexOf("/");
        return path.substring(start + 1, path.length());
    }
}
