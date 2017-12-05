package com.ewedo.facerecognition.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.VideoView;

import com.ewedo.facerecognition.R;
import com.ewedo.libserver.SimpleWebServer;

import java.io.IOException;

/**
 * Created by fozei on 17-12-1.
 */

public class VideoCacheActivity extends Activity {

    private VideoView vv;
    private SimpleWebServer server;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cache);
//        vv = findViewById(R.id.vv);
        server = new SimpleWebServer("192.168.27.13", 20000, Environment.getExternalStorageDirectory(), false);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("***", "VideoCacheActivity.onStart: " + server.isAlive());
        String hostname = server.getHostname();
        Log.i("***", "VideoCacheActivity.onStart: " + server.getListeningPort());
    }
}
