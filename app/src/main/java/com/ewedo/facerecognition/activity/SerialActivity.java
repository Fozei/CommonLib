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
                                SystemClock.sleep(1000);
                                helper.sendHex("05");
                            }
                        });

                    }
                }
                if (comBean.bRec.length == 9) {
                    if (comBean.bRec[3] == (byte) 0x35 && comBean.bRec[4] == (byte) 0x32) {
                        byte operationResult = comBean.bRec[6];
                        switch (operationResult) {
                            case 0x59:
                                Log.i("***", "SerialActivity.onDataReceived: 下载密码成功");
                                //读数据
//                                helper.readSegmentData(comBean.bRec[5], (byte) 0x00);
//                                helper.readSegmentData(comBean.bRec[5], (byte) 0x01);
//                                helper.readSegmentData(comBean.bRec[5], (byte) 0x02);
                                helper.readSegmentData(comBean.bRec[5], (byte) 0x03);
                                //写数据


                                break;
                            case 0X30:
                                Log.i("***", "SerialActivity.onDataReceived: 寻不到射频卡");
                                break;
                            case 0X33:
                                Log.i("***", "SerialActivity.onDataReceived: 密码错误");
                                break;
                        }
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
                    helper.checkPassA((byte) 0x00, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
//                    helper.checkPassB((byte) 0x00, new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
//                    helper.readSegmentData((byte) 0x00, (byte) 0x00);
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

    //读数据返回
    //开始 长度 长度 命令  命令 扇区 块号 状态字 |---------------------------- 数据 ------------------------------------| 结束 BCC
    //[2,  0,  21,  53,  51,  0,  0,  89,   -117, -23, -125, 35, -62, 8, 4, 0, 98, 99, 100, 101, 102, 103, 104, 105, 3,  71]
    //[2,  0,  21,  53,  51,  0,  1,  89,     0,    0,   0,   0,   0, 0, 0, 0,  0,  0,   0,   0,  0,    0,   0,  0,  3,  74]
    //[2,  0,  21,  53,  51,  0,  2,  89,     0,    0,   0,   0,   0, 0, 0, 0,  0,  0,   0,   0,  0,    0,   0,  0,  3,  73]
    //[2,  0,  21,  53,  51,  0,  3,  89,     0,    0,   0,   0,   0, 0, -1,7, -128,105, -1, -1, -1,   -1,  -1,  -1, 3,  89]
}
