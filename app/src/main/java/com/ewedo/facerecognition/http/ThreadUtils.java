package com.ewedo.facerecognition.http;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by fozei on 17-10-26.
 */

public class ThreadUtils {
    public static final Handler mHandler = new Handler(Looper.getMainLooper());

    public ThreadUtils() {
    }

    public static Thread getMainThread() {
        return Looper.getMainLooper().getThread();
    }

    public static boolean isRunInMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static void runInMainThread(Runnable runnable) {
        if (runnable != null) {
            mHandler.post(runnable);
        }

    }

    public static void runInMainThreadLater(Runnable runnable, long delayMillis) {
        if (runnable != null) {
            mHandler.postDelayed(runnable, delayMillis);
        }

    }

    public static void cancelRunnableInMainThread(Runnable runnable) {
        if (runnable != null) {
            mHandler.removeCallbacks(runnable);
        }

    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
