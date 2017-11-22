package com.ewedo.libserialhelper.util;

import com.ewedo.libserialhelper.Constants;

/**
 * Created by fozei on 17-11-16.
 */

class CmdBuilder {
    /**
     * 拼接没有数据包的简单命令
     *
     * @param cmd 要发送的指令
     * @return 拼接结果
     */
    public static byte[] buildSimpleCmd(byte[] cmd) {
        //STX + Length + Length + cmd.length + ETX + BCC
        byte[] result = new byte[cmd.length + 5];
        int index = 0;
        result[index++] = Constants.STX;
        result[index++] = (byte) (cmd.length >> 8);
        result[index++] = (byte) cmd.length;
        for (byte aCmd : cmd) {
            result[index++] = aCmd;
        }
        result[index++] = Constants.ETX;

        result[index] = generateBcc(result);
        return result;
    }

    public static byte[] buildCmdWithData(byte[] cmd, byte segment, byte[] pwd) {
        //0x02 0x00 0x09        0x35        0x32        扇区号 6 byte hex 密码 0x03
        byte[] result = new byte[cmd.length + 6 + pwd.length];
        int index = 0;
        result[index++] = Constants.STX;
        result[index++] = (byte) (cmd.length >> 8);
        result[index++] = (byte) (cmd.length + pwd.length + 1);
        for (byte aCmd : cmd) {
            result[index++] = aCmd;
        }
        result[index++] = segment;
        for (byte aPwd : pwd) {
            result[index++] = aPwd;
        }
        result[index++] = Constants.ETX;

        result[index] = generateBcc(result);
        return result;
    }

    public static byte[] buildReadDataCmd(byte[] cmd, byte segment, byte bound) {
        //0x02        0x00        0x04        0x35        0x33        扇区号 块号        0x03        BCC
        byte[] result = new byte[9];
        int index = 0;
        result[index++] = Constants.STX;
        result[index++] = (byte) (0x00);
        result[index++] = (byte) (0x04);
        for (byte aCmd : cmd) {
            result[index++] = aCmd;
        }
        result[index++] = segment;
        result[index++] = bound;
        result[index++] = Constants.ETX;
        result[index] = generateBcc(result);
        return result;
    }

    private static byte generateBcc(byte[] result) {
        byte bcc = result[0];
        for (int i = 1; i < result.length; i++) {
            bcc ^= result[i];
        }
        return bcc;
    }
}
