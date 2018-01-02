package com.ewedo.facerecognition;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
//        SimpleWebServer server = new SimpleWebServer("192.168.27.5", 50001, new File("/storage/"), true);
//        try {
//            server.start();
//        } catch (IOException e) {
//            Log.i("***", "MainActivity.onCreate: " + e.getMessage());
//            e.printStackTrace();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                print();
            }
        } else {
            print();
        }


        Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri internalContentUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                print();
            }
        }
    }

    private void print() {
        String columns[] = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
        Log.i("***", "MainActivity.onCreate: " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int dataIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int displayNameIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);

                if (cursor.getString(dataIndex).substring(
                        cursor.getString(dataIndex).lastIndexOf("/") + 1,
                        cursor.getString(dataIndex).lastIndexOf("."))
                        .replaceAll(" ", "").length() <= 0) {
                } else {
                    String id = cursor.getString(idIndex);
                    String data = cursor.getString(dataIndex);
                    Log.i("***", "MainActivity.print: " + data);
                    String duration = cursor.getString(durationIndex);
                    String title = cursor.getString(titleIndex);
                    String album = cursor.getString(albumIndex);
                    String albumId = cursor.getString(albumIdIndex);
                    String artist = cursor.getString(artistIndex);
                    String displayName = cursor.getString(displayNameIndex);
                }
            } while (cursor.moveToNext());
        }
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
