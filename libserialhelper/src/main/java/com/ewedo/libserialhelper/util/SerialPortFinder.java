/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.ewedo.libserialhelper.util;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SerialPortFinder {

    private static final String TAG = "SerialPort";
    private List<Driver> mDrivers = null;

    /**
     * 获取/proc/tty/drivers目录下标记为serial的所有驱动，第二个字段为设备的位置：/dev/***
     *
     * @return 所有标记为serial驱动组成的list
     * @throws IOException
     */
    private List<Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new ArrayList<>();
            //proc文件系统是一个伪文件系统，它只存在内存当中，而不占用外存空间。
            //用户和应用程序可以通过proc得到系统的信息，并可以改变内核的某些参数。
            // 由于系统的信息，如进程，是动态改变的，所以用户或应用程序读取proc文件时，proc文件系统是动态从系统内核读出所需信息并提交的。

            //终端是一种字符型设备，它有多种类型，通常使用tty来简称各种类型的终端设备。tty是Teletype的缩写。
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null) {
                // Issue 3:
                // Since driver name may contain spaces, we do not extract driver name with split()
                String driverName = l.substring(0, 0x15).trim();
                /*
                 *   pty_master           /dev/ptm      128 0-1048575 pty:master
                */
                String[] w = l.split(" +");

                if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
                    Log.d(TAG, "Found new driver " + driverName + " on " + w[w.length - 4]);
                    mDrivers.add(new Driver(driverName, w[w.length - 4]));
                }
            }
            r.close();
        }
        return mDrivers;
    }

    public String[] getAllDevices() {
        List<String> devices = new ArrayList<>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                for (File file : driver.getDevices()) {
                    String device = file.getName();
                    String value = String.format("%s (%s)", device, driver.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    public String[] getAllDevicesPath() {
        List<String> devices = new ArrayList<>();
        // Parse each driver
        Iterator<Driver> itdriv;
        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                for (File file : driver.getDevices()) {
                    String device = file.getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    /**
     * 通过读取/proc/tty/drivers目录下驱动信息，获取串口驱动设备位置
     * 通过在此类中遍历设备根目录/dev下所有文件，匹配到驱动中有的，即是真实存在的设备
     */
    public class Driver {
        List<File> mDevices = null;
        private String mDriverName;
        private String mDeviceRoot;

        Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        List<File> getDevices() {
            if (mDevices == null) {
                mDevices = new ArrayList<>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                int i;
                for (i = 0; i < files.length; i++) {
                    if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
                        Log.d(TAG, "Found new device: " + files[i]);
                        mDevices.add(files[i]);
                    }
                }
            }
            return mDevices;
        }

        public String getName() {
            return mDriverName;
        }
    }
}
