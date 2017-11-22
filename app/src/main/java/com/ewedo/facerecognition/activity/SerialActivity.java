package com.ewedo.facerecognition.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ewedo.facerecognition.R;
import com.ewedo.libserialhelper.Constants;
import com.ewedo.libserialhelper.module.ComBean;
import com.ewedo.libserialhelper.util.SerialHelper;
import com.ewedo.libserialhelper.util.SerialPortFinder;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fozei on 17-11-16.
 */

public class SerialActivity extends Activity {

    private SerialHelper helper;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);
        SerialPortFinder finder = new SerialPortFinder();
        String[] allDevices = finder.getAllDevices();
        for (int i = 0; i < allDevices.length; i++) {
            Log.i("***", "SerialActivity.onCreate devices : " + allDevices[i]);
        }
        String[] allDevicesPath = finder.getAllDevicesPath();
        for (int i = 0; i < allDevicesPath.length; i++) {
            Log.i("***", "SerialActivity.onCreate path: " + allDevicesPath[i]);
        }

        helper = new SerialHelper("/dev/ttyS3", 9600) {

            @Override
            protected void onDataReceived(ComBean comBean) {
                Log.i("***", "SerialActivity.onDataReceived: " + comBean);
                if (comBean.bRec.length == 1) {
                    if (comBean.bRec[0] == Constants.ACK) {
                        Log.i("***", "SerialActivity.onDataReceived: -----");
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                SystemClock.sleep(3000);
                                helper.sendHex("05");
                            }
                        });

                    }
                }
            }
        };
        try {
            helper.open();
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
//            helper.sendAutoDetect();
//            helper.sendReset();
//            helper.sendSearchM1Card();
//            helper.getM1Sn();
//            helper.checkPassA((byte) 0x00, new byte[]{0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F});
//                    helper.checkPassB((byte) 0x00, new byte[]{0x0F, 0x0F, 0x0F, 0x0F, 0x0F, 0x0F});
                    helper.readSegmentData((byte) 0x00, (byte) 0x00);
                }
            }, 3000);

        } catch (IOException e) {
            Log.i("***", "SerialActivity.onCreate: " + e.getMessage());
            e.printStackTrace();
        }

    }
    //十进制复位返回值[02000230300303]
    //开始 长度 长度  30  30  |       ---               数据包                           --- |
    //[2,  0,   18, 48, 48, 65, 67, 84, 32, 51, 49, 48, 95, 82, 49, 32, 86, 49, 46, 48, 55, 3, 5]


    //十进制自动检测卡类型 返回值[02000231310303]
    //开始 长度 长度 命令 命令参数  卡类型状态字  卡类型状态字 结束 BCC
    //[2,  0,  4,   49, 49,        48,           57,     3, 12]

    //寻卡操作返回 hex：[02    00  02  35  30  03  06]
    //                        操作状态 P
    //十进制 [2, 0, 3, 53, 48,    78,     3, 73]
    //      [2, 0, 3, 53, 48,    89,    3, 94]
}
