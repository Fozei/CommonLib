package com.ewedo.ziputil;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


/**
 * Created by fozei on 17-12-13.
 */

public class ResourceUtil {
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

    private static final int MEETING_HALL = 0;
    private static final int SENCE_1 = 1;
    private static final int SENCE_2 = 2;
    private static final int SENCE_3 = 3;
    private static final int SENCE_4 = 4;
    private static final int SENCE_5 = 5;
    private static final int SENCE_6 = 6;
    private static final int SENCE_7 = 7;
    private static final int SENCE_8 = 8;


    public static void deCompressResource(final Activity activity, final ResourceUtilCallback callback, final int... programID) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String rootDir = getRootDir(activity);
                    for (int aProgramID : programID) {
                        String targetDir = rootDir + File.separator + aProgramID;
                        unZipAssets(activity, targetDir, aProgramID);
                    }

                    if (callback != null && (!activity.isFinishing())) {
                        String[] results = new String[programID.length];
                        for (int i = 0; i < results.length; i++) {
                            results[i] = rootDir + File.separator + programID[i];
                        }
                        callback.onResourceReady(results);
                    } else {
                        throw new NullPointerException("call back is null");
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            }
        }.start();
    }

    private static String getRootDir(Activity activity) {
        return activity.getFilesDir().toString() + File.separator + "rootRes";
    }

    /**
     * 解压缩一个文件
     *
     * @param zipFile    压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void unZipFile(File zipFile, String folderPath) throws IOException {
        File desDir = new File(folderPath);
        desDir.deleteOnExit();
        desDir.mkdirs();
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            String name = entry.getName();
            if (name.endsWith(File.separator))
                continue;
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("UTF-8"), "UTF-8");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[BUFF_SIZE];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
        zf.close();
    }

    public static void unZipAssets(Context context, String desDir, int programID) throws IOException {
        File file = new File(desDir);

        if (!file.exists()) {
            file.mkdirs();
        }

        String resName = programID + ".zip";

        InputStream inputStream = context.getAssets().open(resName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        byte[] buffer = new byte[1024 * 1024];
        int count = 0;
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                file = new File(desDir + File.separator + zipEntry.getName());
                if (!file.exists()) {
                    file.mkdir();
                }
            } else {
                file = new File(desDir + File.separator + zipEntry.getName());
                if (!file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    public static void unZip(Context context, String assetName, String outputDirectory, boolean isReWrite) throws IOException {
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        InputStream inputStream = context.getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        // 使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            // 如果是一个目录
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者是文件不存在
                if (isReWrite || !file.exists()) {
                    file.mkdir();
                }
            } else {
                // 如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者文件不存在，则解压文件
                if (isReWrite || !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }


    public static void reset(final Activity activity, final ResourceUtilCallback callback, final int... programID) {
        new Thread() {
            @Override
            public void run() {
                String rootPath = getRootDir(activity);
                File rootDir = new File(rootPath);
                if (rootDir.exists()) {
                    deleteDir(rootDir);
                }

                try {
                    for (int aProgramID : programID) {
                        unZipAssets(activity, rootPath, aProgramID);
                    }

                    if (callback != null && !activity.isFinishing()) {
                        String[] results = new String[programID.length];
                        for (int i = 0; i < results.length; i++) {
                            results[i] = rootDir + File.separator + programID[i];
                        }
                        callback.onResourceReady(results);
                    } else {
                        throw new NullPointerException("call back is null");
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            }
        }.start();
    }

    private static void deleteDir(File rootDir) {
        File[] files = rootDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                deleteDir(files[i]);
            }
            files[i].delete();
        }
        rootDir.delete();
    }
}
