package com.wanmi.sbc.mongo.oplog.context.handler;

import com.mongodb.DBObject;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
   * @Description:  MongoDB 系统无用日志数据筛选
   * @Author: ZhangLingKe
   * @CreateDate: 2019/8/14 17:31
   */
@Component
public class OplogUnUseDataFilterHandler {


    private static final String FROM_MIGRATE = "fromMigrate";
    private static final String OPLOG_KEY_OP = "op";
    private static final String OPLOG_KEY_OP_VALUE_N = "n";
    private static final String OPLOG_KEY_NS = "ns";
    private static final String OPOINT = ".";
    private static final String COLLECTION_START = "system.";
    private static final String COLLECTION_END = ".chunks";



    /**
     * 是否应该跳过这条oplog记录
     *      如下情况会跳过
     *              1.空操作，即op为n
     *              2.ns字段不包含[.]
     *              3.表名以[system.]开头
     *              4.表名以[.chunks]结尾
     *
     * @param  item  oplog记录
     * @return true：跳过
     */
    public boolean shouldSkip(DBObject item) {
        if (item.containsField(FROM_MIGRATE)) {
            return true;
        }
        String op = (String) item.get(OPLOG_KEY_OP);
        if (OPLOG_KEY_OP_VALUE_N.equals(op)) {
            return true;
        }
        String ns = (String) item.get(OPLOG_KEY_NS);
        if (ns == null || !ns.contains(OPOINT)) {
            return true;
        }
        String collection = ns.split("\\.")[1];
        if (collection.startsWith(COLLECTION_START)) {
            return true;
        }
        if (collection.endsWith(COLLECTION_END)) {
            return true;
        }
        return false;
    }

    public boolean isTransaction(Map<String,Object> map){
        String op = (String)map.get("op");
        String ns = (String)map.get("ns");
        if("c".equals(op)&& "admin.$cmd".equals(ns)){
            return true;
        }
        return false;
    }


}
