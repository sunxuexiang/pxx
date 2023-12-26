package com.wanmi.sbc.mongo.oplog.context.handler;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.wanmi.sbc.mongo.oplog.data.OplogData;
import com.wanmi.sbc.mongo.oplog.data.RowBehavior;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
   * @Description:    MongoDB 日志数据解析器
   * @Author: ZhangLingKe
   * @CreateDate: 2019/8/14 17:30
   */
@Slf4j
@Component
public class OplogParserHandler {

    private static final String OP_INSERT = "i";
    private static final String OP_UPDATE = "u";
    private static final String OP_DELETE = "d";

     public OplogData process(Map<String,Object> dbObject) {

         //操作所在的namespace
         String ns = (String) dbObject.get("ns");

         //1字节的操作类型 i d u
         String op = (String) dbObject.get("op");

         //[ 库名,表明 ]
         String[] arr = ns.split("\\.");
         if (arr.length < 2) {
             log.warn("wrong format of ns:{}", ns);
             return null;
         }

         //操作的数据集合
         BasicDBObject basicDBObject = (BasicDBObject) dbObject.get("o");
         String data = basicDBObject.toString();
         String _id = null;
         RowBehavior opType = null;
         String condition = null;
         if (OP_INSERT.equals(op)) {
             opType = RowBehavior.INSERT;
             _id = basicDBObject.get("_id").toString();
         } else if (OP_UPDATE.equals(op)) {
             opType = RowBehavior.UPDATE;
             condition = dbObject.get("o2").toString();

             _id = ((BasicDBObject)dbObject.get("o2")).get("_id").toString();
         } else if (OP_DELETE.equals(op)) {
             opType = RowBehavior.DELETE;
             _id = basicDBObject.get("_id").toString();
         }
         if (data != null && opType != null) {
             return  new OplogData(opType, arr[0], arr[1], data, condition,_id);
         }
         return null;
     }
}
