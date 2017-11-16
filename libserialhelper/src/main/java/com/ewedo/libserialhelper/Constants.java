package com.ewedo.libserialhelper;

/**
 * Created by fozei on 17-11-16.
 */

public class Constants {
    // 传输控制字符
    public static int STX = 0x02;
    //卡机肯定响应
    public static int ACK = 0x06;
    //卡机否定响应
    public static int NAK = 0x15;
    //主机确认
    public static int ENQ = 0x05;
    public static int ETX = 0x03;
    //主机取消
    public static int EOT = 0x04;

    //card type
    //    卡类型状态字S1            卡类型状态字S2    卡类型说明
//          ‘N’  78                ‘1’   48         未知卡类型
//          ‘0’                    ‘0’   48     卡为非接触式射频卡(S50 射频卡)
//          ‘0’                    ‘1’   49     卡为非接触式 S70 射频卡
//          ‘0’                    ‘2’   50     卡为非接触式 UL 射频卡
//          ‘0’                    ‘4’   51     卡为 ISO1443 TYPEA CPU 卡
//          ‘0’                    ‘5’   52     卡为 ISO1443 TYPEB CPU 卡
//          ‘0’                    ‘9’   57     卡为非接触式射频卡但未知类型
    public static int TYPE_SEGMENT_A_UNKDOWN = 78;
    public static int TYPE_SEGMENT_A_KNOWN = 48;

    public static int TYPE_SEGMENT_B_S50 = 48;
    public static int TYPE_SEGMENT_B_S70 = 49;
    public static int TYPE_SEGMENT_B_UL = 50;
    public static int TYPE_SEGMENT_B_TYPEA = 51;
    public static int TYPE_SEGMENT_B_TYPEB = 52;
    public static int TYPE_SEGMENT_B_PART_KNOWN = 57;
}
