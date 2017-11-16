package com.ewedo.libserialhelper.util;

/**
 * Created by fozei on 17-11-16.
 */

public class MyFunc {
    /**
     * @param num 长度
     * @return 是奇数还是偶数
     * 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
     */
    public static int isOdd(int num) {
        return num & 0x1;
    }

    //Hex字符串转int
    static public int HexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    //Hex字符串转byte
    public static byte HexToByte(String inHex) {
        int result = Integer.parseInt(inHex, 16);
        return (byte) result;
    }

    //1字节转2个Hex字符
    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", inByte).toUpperCase();
    }

    //字节数组转转hex字符串
    static public String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        int j = inBytArr.length;
        for (byte anInBytArr : inBytArr) {
            strBuilder.append(Byte2Hex(anInBytArr));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    //字节数组转转hex字符串，可选长度
    static public String ByteArrToHex(byte[] inBytArr, int offset, int byteCount) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = offset; i < byteCount; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
        }
        return strBuilder.toString();
    }

    //转hex字符串转字节数组
    static public byte[] HexToByteArr(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {//奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {//偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            String substring = inHex.substring(i, i + 2);
            result[j] = HexToByte(substring);
            j++;
        }
        return result;
    }
}