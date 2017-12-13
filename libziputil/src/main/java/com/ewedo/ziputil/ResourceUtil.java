package com.ewedo.ziputil;

import android.content.Context;
import android.util.Log;

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


//    /**
//     * 解压缩一个文件
//     *
//     * @throws IOException 当解压缩过程出错时抛出
//     */
//    public static void unZipFile(Context context) throws IOException {
//        // 创建解压目标目录
//        File filesDir = context.getFilesDir();
//        File file = new File(filesDir+ File.separator +"resources");
//        // 如果目标目录不存在，则创建
//        Log.i("***", "ResourceUtil.unZipFile: " + file);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//
//        InputStream inputStream = context.getAssets().open("1.zip");
//        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
//        // 读取一个进入点
//        ZipEntry nextEntry = zipInputStream.getNextEntry();
//        byte[] buffer = new byte[1024 * 1024];
//        int count = 0;
//        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
//        while (nextEntry != null) {
//            // 如果是一个文件夹
//            if (nextEntry.isDirectory()) {
//                file = new File(nextEntry.getName());
//                Log.i("***", "ResourceUtil.unZipFile: directory : "+ file);
//                if (!file.exists()) {
//                    file.mkdir();
//                }
//            } else {
//                // 如果是文件那就保存
//                file = new File(nextEntry.getName());
//                Log.i("***", "ResourceUtil.unZipFile file : " + file);
//                // 则解压文件
//                if (!file.exists()) {
//                    file.createNewFile();
//                    FileOutputStream fos = new FileOutputStream(file);
//                    while ((count = zipInputStream.read(buffer)) != -1) {
//                        fos.write(buffer, 0, count);
//                    }
//
//                    fos.close();
//                }
//            }
//            nextEntry = zipInputStream.getNextEntry();
//        }
//        zipInputStream.close();
//
//
//
//
//    }

    public static void deCompressResource(final Context context, final int type, final int progrem, ResourceReadyCallback callback) {
        new Thread() {
            @Override
            public void run() {
                try {
                    unZipAssets(context, type, progrem);
                } catch (Exception e) {
                    Log.i("***", "ResourceUtil.run: Error " + e.getMessage());
                }
            }
        }.start();
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

    /**
     * 解压缩一个文件
     *
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void unZipAssets(Context context, int type, int progrem) throws IOException {
        File file = new File(context.getFilesDir(), "resource");
        String rootDirectory = file.toString();

        if (!file.exists()) {
            file.mkdirs();
        }

        String[] list = context.getAssets().list("/assets");
        for (int i = 0; i < list.length; i++) {
            Log.i("***", "ResourceUtil.unZipAssets: " + list[i]);
        }

        InputStream inputStream = context.getAssets().open("1.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        byte[] buffer = new byte[1024 * 1024];
        int count = 0;
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                file = new File(rootDirectory + File.separator + zipEntry.getName());
                if (!file.exists()) {
                    file.mkdir();
                }
            } else {
                file = new File(rootDirectory + File.separator + zipEntry.getName());
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


}
