package com.ewedo.facerecognition.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ewedo.facerecognition.R;
import com.ewedo.facerecognition.widget.IjkVideoView;

/**
 * Created by fozei on 17-12-1.
 */

public class VideoCacheActivity extends Activity {

    private IjkVideoView vv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_cache);
        vv = findViewById(R.id.vv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        vv.setVideoURI(Uri.parse("http://192.168.0.106:8000/match.mp4"));
        vv.start();

    }
}
