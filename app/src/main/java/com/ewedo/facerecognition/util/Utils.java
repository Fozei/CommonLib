package com.ewedo.facerecognition.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by fozei on 17-12-4.
 */

public class Utils {
    public static File getVideoCacheDir(Context context) {
        File file = new File(context.getExternalCacheDir(), "video-cache");
        Log.i("***", "Utils.getVideoCacheDir: " + file);
        return file;
    }

    public static void cleanVideoCacheDir(Context context) throws IOException {
        File videoCacheDir = getVideoCacheDir(context);
        cleanDirectory(videoCacheDir);
    }

    private static void cleanDirectory(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        File[] contentFiles = file.listFiles();
        if (contentFiles != null) {
            for (File contentFile : contentFiles) {
                delete(contentFile);
            }
        }
    }

    private static void delete(File file) throws IOException {
        if (file.isFile() && file.exists()) {
            deleteOrThrow(file);
        } else {
            cleanDirectory(file);
            deleteOrThrow(file);
        }
    }

    private static void deleteOrThrow(File file) throws IOException {
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                throw new IOException(String.format("File %s can't be deleted", file.getAbsolutePath()));
            }
        }
    }

    public static String convertIs2String(InputStream inputStream) {
        if (inputStream == null) {
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
