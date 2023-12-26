package com.wanmi.sbc.common.util.auth;


import java.io.*;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-05-22 14:28
 */
public class AuthHelp {

    private static final String ENCODE = "UTF-8";
    public static final int size = 8;


    public static   byte[] getByte(InputStream in) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            bos.write(buffer, 0, length);
        }
        bos.close();
        return bos.toByteArray();
    }
    public static byte[] getProcessByte(byte[] data) throws Exception{
        byte[] lData = new byte[size];
        System.arraycopy(data,0,lData,0,size);
        int processLength = Integer.parseInt(new String(lData));
        byte[] processData = new byte[processLength];
        System.arraycopy(data,size,processData,0,processLength);

        return processData;

    }
}
