package com.ewedo.facerecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = new Intent("fozei.intent.action.SERIAL");
//        startActivity(intent);
        Intent intent = new Intent("fozei.intent.action.CACHE");
        startActivity(intent);
    }
}
