package com.ewedo.facerecognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ewedo.ziputil.ResourceUtil;
import com.ewedo.ziputil.ResourceUtilCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = new Intent("fozei.intent.action.SERIAL");
//        startActivity(intent);
//        Intent intent = new Intent("fozei.intent.action.CACHE");
//        startActivity(intent);
        ResourceUtilCallback callback = new ResourceUtilCallback() {
            @Override
            public void onResourceReady(String[] path) {
                for (int i = 0; i < path.length; i++) {
                    Log.i("***", "MainActivity.onResourceReady: " + path[i]);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        };
        ResourceUtil.deCompressResource(this, callback, 0, 1);
    }

    public void onClick(View view) {
        ResourceUtilCallback callback = new ResourceUtilCallback() {
            @Override
            public void onResourceReady(String[] path) {
                for (int i = 0; i < path.length; i++) {
                    Log.i("***", "MainActivity.onResourceReady: " + path[i]);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Log.i("***", "MainActivity.onError: " + e);
            }
        };
        ResourceUtil.reset(this, callback, 1);
    }
}
