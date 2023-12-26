package com.wanmi.sbc.setting.util;

public class ByteUtils {

    /**
     * 字节转kb/mb/gb
     * @param size
     * @return
     */
    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        Double appSize = Double.NaN;
        if (size < 1024) {
            return String.valueOf(size);
        } else {
            appSize = Double.valueOf( size / 1024);
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (appSize < 1024) {
            return String.format("%.1f", appSize);
        } else {
            appSize = Double.valueOf( appSize / 1024);
        }
        if (appSize < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            return String.format("%.1f", appSize);
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            appSize = Double.valueOf( appSize / 1024);
            return String.format("%.2f", appSize);
        }
    }
}
