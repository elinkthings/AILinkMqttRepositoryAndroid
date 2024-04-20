package com.elinkthings.ailinkmqttdemo;

import java.nio.charset.StandardCharsets;

/**
 * xing<br>
 * 2019/3/7<br>
 * byte,String 工具类
 */
public class BleStrUtils {


    /**
     * (16进制)
     * BLE蓝牙返回的byte[]
     * byte[]转字符串
     */
    public static String byte2HexStr(byte[] b) {
        if (b == null) {
            return "";
        }
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte aB : b) {
            int a = aB & 0xFF;
            stmp = getHexString(a);
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
            hs.append(" ");
        }
        return hs.toString();
    }

    public static String byte2HexStrToUpperCase(byte[] b) {
        if (b == null) {
            return "";
        }
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte aB : b) {
            int a = aB & 0xFF;
            stmp = getHexString(a).toUpperCase();
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString();
    }



    /**
     * int  10进制转16进制(返回大写字母)
     *
     * @return 16进制String
     */
    public static String getHexString(int number) {
        return Integer.toHexString(number);
    }

    /**
     * 将字符串转为16进制
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制String转换成Byte[] * @param hexString the hex string * @return byte[]
     */
    public static byte[] stringToBytes(String string) {
        String hexString = str2HexStr(string);
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] stringToByte(String string) {

        if (string == null || string.equals("")) {
            return null;
        }
        string = string.toUpperCase();
        int length = string.length() / 2;
        char[] hexChars = string.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * b为传入的字节，start是起始位，length是长度，如要获取bit0-bit4的值，则start为0，length为5
     *
     * @param b      byte
     * @param start  取bit开始位置
     * @param length 取bit的长度
     * @return bit相加的值
     */
    public static int getBits(byte b, int start, int length) {
        return ((b >> start) & (0xFF >> (8 - length)));
    }


    /**
     * b为传入的字节,取出bit每个值
     *
     * @param b 字节
     * @return bit数组
     */
    public static int[] getBits(byte b) {
        int[] bits = new int[8];
        for (byte i = 0; i < 8; i++) {
            bits[i] = (b >> i) & 0x01;
        }
        return bits;
    }

    /**
     * 小端序字节数组转Mac地址
     *
     * @param bytes 字节
     * @return {@link String}
     */
    public static String littleBytes2MacStr(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; i--) {
            if (i != 0) {
                stringBuilder.append(String.format("%02X", bytes[i]).toUpperCase()).append(":");
            } else {
                stringBuilder.append(String.format("%02X", bytes[i]).toUpperCase());
            }
        }
        return String.valueOf(stringBuilder);
    }
}
