package com.vlusi.klintelligent.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ReceiveDataManager {
    static String tag = "FW ReceiveDataManager ";

//	// 起始标志
//	final static int STR = 0xAA;
//	// ESC 转义符
//	static int ESC = 0xAB;
//	// 结束标志
//	static int END = 0xAC;
//
//	// 起始标志
//	static int STR_ESC = 0xA1;
//	// ESC 转义符
//	static int ESC_ESC = 0xA2;
//	// 结束标志
//	static int END_ESC = 0xA3;


    /**
     * 根据文件路径，把文件转换成byte数组
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getfilePathtoByte(File file) throws IOException {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            Log.e(tag, "文件太大了");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] array = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < array.length && (numRead = fi.read(array, offset, array.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != array.length) {
            throw new IOException("不可能完全读取的文件 "
                    + file.getName());
        }
        fi.close();
        return array;
    }


    /**
     * 根据指定的协议
     *
     * @param bytes
     * @return
     */
    public static boolean checkData(byte[] bytes) {

        if (bytes[0] != (byte) CMD.STR) {
            Log.e(tag, "起始位失败");
            return false;
        }

        if (bytes[bytes.length - 1] != (byte) CMD.END) {
            Log.e(tag, "结束位失败");
            return false;
        }

        byte CK = 0;

        for (int i = 2; i < bytes.length - 2; i++) {
            CK += bytes[i];
        }
        CK = (byte) ~CK;
        if (bytes[bytes.length - 2] != CK) {
            Log.e(tag, "校验和 校验");
            return true;
        }
        return true;
    }

    /**
     *  解包
     * @param bytes   数据源
     * @param length   数据源的长度
     * @return
     */
    public static byte[] unPackage(byte[] bytes, int length) {


        byte[] newbytes = new byte[length - 4];

        System.arraycopy(bytes, 2, newbytes, 0, length - 4);

        return newbytes;
    }

    /**
     * 转义
     * @param bytes
     * @return
     */
    public static byte[] replaceESC(byte[] bytes) {

        String string = Bytes2HexString(bytes);

        string = string.replaceAll("ABA1", "AA");
        string = string.replaceAll("ABA2", "AB");
        string = string.replaceAll("ABA3", "AC");

        return stringToBytes(string);
    }


    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static int bytesToShort(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8));
        return value;
    }


    /**
     *
     * @param dst  目标数组
     * @param src  源数组
     * @return
     */
    public static byte[] appendBytes(byte[] dst, byte[] src) {
        byte[] newBytes = new byte[dst.length + src.length];
        System.arraycopy(dst, 0, newBytes, 0, dst.length);
        System.arraycopy(src, 0, newBytes, dst.length, src.length);
        return newBytes;
    }

    // 从字节数组到十六进制字符串转换
    public static String Bytes2HexString(byte[] b) {
        final byte[] hex = "0123456789ABCDEF".getBytes();
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    /**
     * 根据String的长度转换成byte数组
     * @param str
     * @return
     */
    public static byte[] stringToBytes(String str) {
        str = str.toUpperCase();

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() - 1; i += 2) {
            String subString = str.substring(i, i + 2);
            int value = 0;
            for (int j = 0; j < 2; j++) {
                switch (subString.charAt(j)) {
                    case '0':
                        if (j == 0) {
                            value = value + 16 * 0;
                        } else {
                            value = value + 0;
                        }
                        break;
                    case '1':
                        if (j == 0) {
                            value = value + 16 * 1;
                        } else {
                            value = value + 1;
                        }
                        break;

                    case '2':
                        if (j == 0) {
                            value = value + 16 * 2;
                        } else {
                            value = value + 2;
                        }
                        break;

                    case '3':
                        if (j == 0) {
                            value = value + 16 * 3;
                        } else {
                            value = value + 3;
                        }
                        break;

                    case '4':
                        if (j == 0) {
                            value = value + 16 * 4;
                        } else {
                            value = value + 4;
                        }
                        break;

                    case '5':
                        if (j == 0) {
                            value = value + 16 * 5;
                        } else {
                            value = value + 5;
                        }
                        break;

                    case '6':
                        if (j == 0) {
                            value = value + 16 * 6;
                        } else {
                            value = value + 6;
                        }
                        break;

                    case '7':
                        if (j == 0) {
                            value = value + 16 * 7;
                        } else {
                            value = value + 7;
                        }
                        break;

                    case '8':
                        if (j == 0) {
                            value = value + 16 * 8;
                        } else {
                            value = value + 8;
                        }
                        break;

                    case '9':
                        if (j == 0) {
                            value = value + 16 * 9;
                        } else {
                            value = value + 9;
                        }
                        break;

                    case 'A':
                        if (j == 0) {
                            value = value + 16 * 10;
                        } else {
                            value = value + 10;
                        }
                        break;
                    case 'B':
                        if (j == 0) {
                            value = value + 16 * 11;
                        } else {
                            value = value + 11;
                        }
                        break;
                    case 'C':
                        if (j == 0) {
                            value = value + 16 * 12;
                        } else {
                            value = value + 12;
                        }
                        break;
                    case 'D':
                        if (j == 0) {
                            value = value + 16 * 13;
                        } else {
                            value = value + 13;
                        }
                        break;
                    case 'E':
                        if (j == 0) {
                            value = value + 16 * 14;
                        } else {
                            value = value + 14;
                        }
                        break;
                    case 'F':
                        if (j == 0) {
                            value = value + 16 * 15;
                        } else {
                            value = value + 15;
                        }
                        break;
                    default:
                        break;
                }
            }
            bytes[i / 2] = (byte) value;
        }

        return bytes;
    }


}
