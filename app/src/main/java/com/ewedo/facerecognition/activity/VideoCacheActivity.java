package com.ewedo.facerecognition.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.VideoView;

import com.ewedo.facerecognition.BaseApplication;
import com.ewedo.facerecognition.R;
import com.fozei.libvideocache.HttpProxyCacheServer;

/**
 * Created by fozei on 17-12-1.
 */

public class VideoCacheActivity extends Activity {

    private VideoView vv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cache);
        vv = findViewById(R.id.vv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        HttpProxyCacheServer proxy = BaseApplication.getProxy(this);
        String proxyUrl = proxy.getProxyUrl("http://192.168.27.9:8000/adv.mp4", false);
        Log.i("***", "VideoCacheActivity.onStart:" + proxyUrl + "\r\n");
        vv.setVideoPath(proxyUrl);
        vv.start();
    }
}
