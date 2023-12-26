package com.wanmi.sbc.mongo.oplog.utils;

import org.bson.types.BSONTimestamp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 *  以本地文件的形式存取时间戳
 *
 *  @author     ：Macx
 *  @Date       ：4/3/2019-3:39 PM
 */
public class TimestampUtil {

    private static String tsFile = "opLog_time";

    /**
     * 本地文件的时间戳获取
     * @return
     */
    public static BSONTimestamp get() {
        BSONTimestamp timestamp = null;
        Path path = Paths.get(tsFile);
        if (!path.toFile().exists()) {
            return null;
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.size() > 0) {
                String line = lines.get(0);
                String[] arr = line.split(":");
                if (arr.length == 2) {
                    int time = Integer.parseInt(arr[0]);
                    int incr = Integer.parseInt(arr[1]);
                    timestamp = new BSONTimestamp(time, incr);
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
//        System.out.println("get:" + timestamp);
        return timestamp;
    }

    /**
     * 本地文件时间戳写入
     * @param timestamp
     */
    public static void set(BSONTimestamp timestamp) {
        int time = timestamp.getTime();
        int incr = timestamp.getInc();
        String strValue = String.format("%d:%d", time, incr);
        Path path = Paths.get(tsFile);
        try {
            Files.write(path, strValue.getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
//        System.out.println("set:" + timestamp);
    }

    /**
     * 获取当前时间戳
     * @return
     */
    public static  BSONTimestamp getNow() {
        int time = (int) (System.currentTimeMillis() / 1000);
        int incr = 0;
        BSONTimestamp timestamp = new BSONTimestamp(time, incr);
//        System.out.println("getnow:" + timestamp);
        return timestamp;
    }



}
