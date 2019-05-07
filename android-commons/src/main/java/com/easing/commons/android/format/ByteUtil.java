package com.easing.commons.android.format;

public class ByteUtil {

    //显示字节对应的实际位数据
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    //显示字节对应的实际位数据
    public static String byteToBit(byte[] bytes) {
        String bitStr = "";
        for (int i = 0; i < bytes.length; i++)
            bitStr += byteToBit(bytes[i]);
        return bitStr;
    }

    //无符号整数 转 无符号二进制字符
    public static String intToBin(int num) {
        if (num < 0)
            throw new RuntimeException("num should not below 0");
        return Integer.toBinaryString(num);
    }

    //无符号整数 转 无符号十六进制字符
    public static String intToHex(int num) {
        if (num < 0)
            throw new RuntimeException("num should not be below 0");
        return Integer.toHexString(num);
    }

    //无符号整数 转 无符号字节
    public static byte intToByte(int num) {
        if (num < 0 || num > 255)
            throw new RuntimeException("num must between 0 and 255");
        return (byte) num;
    }

    //无符号二进制字符 转 无符号整数
    public static int binToInt(String binStr) {
        if (binStr.length() > 8)
            throw new RuntimeException("length of bin string should not be above 8");
        return Integer.valueOf(binStr, 2);
    }

    //无符号二进制字符 转 无符号字节
    public static byte binToByte(String binStr) {
        return intToByte(binToInt(binStr));
    }

    //无符号十六进制字符 转 无符号整数
    public static int hexToInt(String hexStr) {
        if (hexStr.length() > 2)
            throw new RuntimeException("length of hex string should not be above 2");
        return Integer.valueOf(hexStr, 16);
    }

    //无符号十六进制字符 转 无符号字节
    public static byte hexToByte(String hexStr) {
        return intToByte(hexToInt(hexStr));
    }

    //有符号字节 转 无符号整数
    public static int byteToInt(byte numByte) {
        return numByte & 0xff;
    }

    //有符号字节 转 无符号二进制字符
    public static String byteToBin(byte numByte) {
        return intToBin(byteToInt(numByte));
    }

    //有符号字节 转 无符号十六进制字符
    public static String byteToHex(byte numByte) {
        return intToHex(byteToInt(numByte));
    }

    //无符号整数 转 无符号字节集
    public static byte[] intToByteArray(int num) {
        if (num < 0)
            throw new RuntimeException("num should not be below 0");
        if (num == 0)
            return new byte[]{0};
        int bits = 0;
        while (Math.pow(2, ++bits) <= num)
            continue;
        if (bits % 8 != 0)
            bits = bits + 8 - bits % 8;
        int length = bits / 8;
        byte[] bytes = new byte[length];
        for (int i = length - 1; i >= 0; i--)
            bytes[length - 1 - i] = (byte) (num >> (i * 8));
        return bytes;
    }

    //无符号二进制字符 转 无符号字节集
    public static byte[] binToByteArray(String binStrs) {
        if (binStrs.length() % 8 != 0)
            binStrs = TextUtil.append(binStrs, "0", 8 - binStrs.length() % 8);
        int size = binStrs.length() / 8;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++)
            bytes[i] = ByteUtil.binToByte("" + binStrs.substring(2 * i, 2 * i + 8));
        return bytes;
    }

    //无符号十六进制字符 转 无符号字节集
    public static byte[] hexToByteArray(String hexStrs) {
        if (hexStrs.length() % 2 != 0)
            hexStrs = 0 + hexStrs;
        int size = hexStrs.length() / 2;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++)
            bytes[i] = ByteUtil.hexToByte("" + hexStrs.charAt(2 * i) + hexStrs.charAt(2 * i + 1));
        return bytes;
    }

    //无符号字节集 转 无符号二进制字符
    public static String byteArrayToBin(byte[] bytes) {
        String hexStr = "";
        for (int i = bytes.length - 1; i >= 0; i--)
            hexStr += byteToBin(bytes[i]);
        return hexStr;
    }

    //无符号字节集 转 无符号十六进制字符
    public static String byteArrayToHex(byte[] bytes) {
        String hexStr = "";
        for (int i = bytes.length - 1; i >= 0; i--)
            hexStr += byteToHex(bytes[i]);
        return hexStr;
    }

    //无符号字节集 转 无符号二进制字符
    public static String byteArrayToBin(byte[] bytes, String split) {
        String hexStr = "";
        for (int i = bytes.length - 1; i >= 0; i--) {
            hexStr = hexStr + byteToBin(bytes[i]);
            if (i != 0 && split != null)
                hexStr = hexStr + split;
        }
        return hexStr;
    }

    //无符号字节集 转 无符号十六进制字符
    public static String byteArrayToHex(byte[] bytes, String split) {
        String hexStr = "";
        for (int i = bytes.length - 1; i >= 0; i--) {
            hexStr = hexStr + byteToHex(bytes[i]);
            if (i != 0 && split != null)
                hexStr = hexStr + split;
        }
        return hexStr;
    }

    //按位左移
    public static long LMove(long value, int bit) {
        return value << bit;
    }

    //按位右移
    public static long RMove(long value, int bit) {
        return value >> bit;
    }
}
