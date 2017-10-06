package com.vlusi.klintelligent.utils;

public class SendDataManager {


    public static int getCK(byte[] bytes, int offset, int length) {

        int CK = 0;

        for (int i = 0; i < length; i++) {
            CK += bytes[offset + i];
        }
        CK = ~CK;  //取反

        return CK;
    }

    public static void putShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }

}
