package com.ewedo.libserialhelper.util;

import android.util.Log;

import com.ewedo.libserialhelper.api.SerialPort;
import com.ewedo.libserialhelper.module.ComBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * Created by fozei on 17-11-16.
 */

public abstract class SerialHelper {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private String sPort = "/dev/s3c2410_serial0";
    private int iBaudRate = 9600;
    private boolean _isOpen = false;
    private byte[] _bLoopData = new byte[]{0x30};
    private int iDelay = 500;

    private SerialHelper() {

    }


    //----------------------------------------------------
    public SerialHelper(String sPort, int iBaudRate) {
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }

    public SerialHelper(String sPort) {
        this(sPort, 9600);
    }

    public SerialHelper(String sPort, String sBaudRate) {
        this(sPort, Integer.parseInt(sBaudRate));
    }

    //----------------------------------------------------
    public void open() throws SecurityException, IOException, InvalidParameterException {
        Log.i("***", "SerialHelper.open: " + sPort);
        mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();
        _isOpen = true;
    }

    //----------------------------------------------------
    public void close() {
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        _isOpen = false;
    }

    //----------------------------------------------------
    public void send(byte[] bOutArray) {
        setbLoopData(bOutArray);
        mSendThread.setResume();
//            mOutputStream.write(bOutArray);
    }

    //----------------------------------------------------
    public void sendHex(String sHex) {
        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    //----------------------------------------------------
    public void sendTxt(String sTxt) {
        byte[] bOutArray = sTxt.getBytes();
        send(bOutArray);
    }

    //----------------------------------------------------
    public int getBaudRate() {
        return iBaudRate;
    }

    public boolean setBaudRate(int iBaud) {
        if (_isOpen) {
            return false;
        } else {
            iBaudRate = iBaud;
            return true;
        }
    }

    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    //----------------------------------------------------
    public String getPort() {
        return sPort;
    }

    public boolean setPort(String sPort) {
        if (_isOpen) {
            return false;
        } else {
            this.sPort = sPort;
            return true;
        }
    }

    //----------------------------------------------------
    public boolean isOpen() {
        return _isOpen;
    }

    //----------------------------------------------------
    public byte[] getbLoopData() {
        return _bLoopData;
    }

    //----------------------------------------------------
    public void setbLoopData(byte[] bLoopData) {
        this._bLoopData = bLoopData;
    }

    //----------------------------------------------------
    public void setTxtLoopData(String sTxt) {
        this._bLoopData = sTxt.getBytes();
    }

    //----------------------------------------------------
    public void setHexLoopData(String sHex) {
        this._bLoopData = MyFunc.HexToByteArr(sHex);
    }

    //----------------------------------------------------
    public int getiDelay() {
        return iDelay;
    }

    //----------------------------------------------------
    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }

    //----------------------------------------------------
    public void startSend() {
        if (mSendThread != null) {
            mSendThread.setResume();
        }
    }

    //----------------------------------------------------
    public void stopSend() {
        if (mSendThread != null) {
            mSendThread.setSuspendFlag();
        }
    }

    //----------------------------------------------------
    protected abstract void onDataReceived(ComBean ComRecData);

    public void sendAutoDetect() {
        byte[] autoDetect = {0x31, 0x31};
        byte[] bOutArray = CmdBuilder.buildSimpleCmd(autoDetect);
        Log.i("***", "SerialHelper.sendAutoDetect: " + Arrays.toString(bOutArray));
//        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    public void sendReset() {
        byte[] autoDetect = {0x30, 0x30};
        byte[] bOutArray = CmdBuilder.buildSimpleCmd(autoDetect);
        Log.i("***", "SerialHelper.sendAutoDetect: " + Arrays.toString(bOutArray));
//        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    public void sendSearchM1Card() {
        byte[] autoDetect = {0x35, 0x30};
        byte[] bOutArray = CmdBuilder.buildSimpleCmd(autoDetect);
        Log.i("***", "SerialHelper.sendAutoDetect: " + Arrays.toString(bOutArray));
//        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    public void getM1Sn() {
        byte[] autoDetect = {0x35, 0x31};
        byte[] bOutArray = CmdBuilder.buildSimpleCmd(autoDetect);
        Log.i("***", "SerialHelper.sendAutoDetect: " + Arrays.toString(bOutArray));
//        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    public void checkPassA(byte segment, byte[] pwd) {
        // TODO: 17-11-16 长度校验
        // TODO: 17-11-17 结果返回
        byte[] cmd = {0x35, 0x32};
        byte[] bOutArray = CmdBuilder.buildCmdWithData(cmd, segment, pwd);
        Log.i("***", "SerialHelper.checkPassA: " + Arrays.toString(bOutArray));
//        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    public void checkPassB(byte segment, byte[] pwd) {
        // TODO: 17-11-16 长度校验
        // TODO: 17-11-17 结果返回
        byte[] cmd = {0x35, 0x39};
        byte[] bOutArray = CmdBuilder.buildCmdWithData(cmd, segment, pwd);
        Log.i("***", "SerialHelper.sendAutoDetect: " + Arrays.toString(bOutArray));
        send(bOutArray);
    }

    public void readSegmentData(byte segment, byte bound) {
        byte[] cmd = {0x35, 0x33};
        byte[] bOutArray = CmdBuilder.buildReadDataCmd(cmd, segment, bound);
        Log.i("***", "SerialHelper readSegmentData : " + Arrays.toString(bOutArray));
        send(bOutArray);
    }

    public void writeSegmentData(byte segment, byte area, byte[] data) {
        byte[] cmd = {0x35, 0x34};
        byte[] bOutArray = CmdBuilder.buildWriteDataCmd(cmd, segment, area, data);
        Log.i("***", "SerialHelper writeSegmentData : " + Arrays.toString(bOutArray));
        send(bOutArray);
    }

    //----------------------------------------------------
    private class ReadThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    if (mInputStream == null) return;
                    byte[] buffer = new byte[512];
                    //阻塞式方法
                    int size = mInputStream.read(buffer);
                    if (size > 0) {
                        // TODO: 17-11-16
                        ComBean comBean = new ComBean(sPort, buffer, size);
                        onDataReceived(comBean);
                    }
                    try {
                        Thread.sleep(50);//延时50ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    //----------------------------------------------------
    private class SendThread extends Thread {
        public boolean suspendFlag = true;// 控制线程的执行

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                Log.i("***", "SendThread.run: write++++++++++");
                synchronized (this) {
                    while (suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    mOutputStream.write(getbLoopData());
                    setSuspendFlag();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    Thread.sleep(iDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        //唤醒线程
        public synchronized void setResume() {
            Log.i("***", "SendThread.setResume: notify");
            this.suspendFlag = false;
            notify();
        }
    }
}