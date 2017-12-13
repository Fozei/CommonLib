package com.ewedo.facerecognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ewedo.ziputil.ResourceUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = new Intent("fozei.intent.action.SERIAL");
//        startActivity(intent);
//        Intent intent = new Intent("fozei.intent.action.CACHE");
//        startActivity(intent);

        ResourceUtil.deCompressResource(this, 1, 1, null);
    }
}
