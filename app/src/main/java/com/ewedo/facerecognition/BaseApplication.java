package com.ewedo.facerecognition;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ewedo.facerecognition.util.Utils;
import com.fozei.libvideocache.HttpProxyCacheServer;

/**
 * Created by fozei on 17-10-26.
 */

public class BaseApplication extends Application {
    protected static Context appContext;
    private static RequestQueue mRequestQueue;
    private HttpProxyCacheServer proxy;

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

    public static HttpProxyCacheServer getProxy(Context context) {
        BaseApplication app = (BaseApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(Utils.getVideoCacheDir(this))
                .build();
    }
}
