package com.ewedo.facerecognition;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by fozei on 17-10-26.
 */

public class BaseApplication extends Application {
    protected static Context appContext;
    private static RequestQueue mRequestQueue;

    public static Context getContext() {
        return appContext;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            synchronized (BaseApplication.class) {
                mRequestQueue = Volley.newRequestQueue(appContext);
            }
        }
        return mRequestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }
}
