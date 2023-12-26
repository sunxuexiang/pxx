package com.wanmi.sbc.mongo.oplog.data;

import lombok.Data;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-17
 * \* Time: 11:17
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
@Data
public class Parameter {
    private static final String ROOT_PATH           = "/MongoCapture/destination/%s";
    private static final String RUNNING_PATH        = ROOT_PATH + "/running";
    private static final String POSITION_PATH       = ROOT_PATH + "/position";



    public static enum MetaMode {
        /** 内存存储模式 */
        MEMORY,
        /** 文件存储模式 */
        ZOOKEEPER,
        /** 本地文件存储模式 */
        LOCAL_FILE;

        public boolean isMemory() {
            return this.equals(MetaMode.MEMORY);
        }

        public boolean isZookeeper() {
            return this.equals(MetaMode.ZOOKEEPER);
        }

        public boolean isLocalFile() {
            return this.equals(MetaMode.LOCAL_FILE);
        }
    }

    public static String getRunningPath(String destination){
        return String.format(RUNNING_PATH,destination);
    }

    public static String getPositionPath(String destination){
        return String.format(POSITION_PATH,destination);
    }

    public static String getRootPath(String destination){
        return String.format(ROOT_PATH,destination);
    }

}
