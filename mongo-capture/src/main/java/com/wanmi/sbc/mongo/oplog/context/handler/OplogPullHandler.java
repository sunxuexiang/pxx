package com.wanmi.sbc.mongo.oplog.context.handler;

import com.mongodb.*;
import com.wanmi.sbc.mongo.oplog.comm.factory.MongoFactory;
import org.bson.types.BSONTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

 /**
   * @Description:   拉取OpLog(从指定时间戳处开始)
   * @Author: ZhangLingKe
   * @CreateDate: 2019/8/14 17:30
   */
@Component
public class OplogPullHandler {

    @Autowired
    private MongoFactory factory;

    /**
     * 以TAILABLE方式获取的DBCursor在mongodb driver
     *      内部是以一个while循环不停发送getmore命令到mongodb实现的
     *
     * @param timestamp oplog 起始时间
     * @return          一个持续等待数据、不超时的DBCursor
     */
    public DBCursor oplogCursor(BSONTimestamp timestamp) {
        DBObject query = new BasicDBObject();
        List<String> opList = new ArrayList<String>(){{
            add("i");add("u");add("d");add("c");
        }};

        query.put("ts", new BasicDBObject(QueryOperators.GT, timestamp));
        query.put("op", new BasicDBObject(QueryOperators.IN, opList));
        int options = Bytes.QUERYOPTION_TAILABLE |
                Bytes.QUERYOPTION_AWAITDATA |
                Bytes.QUERYOPTION_NOTIMEOUT |
                Bytes.QUERYOPTION_OPLOGREPLAY;
        return factory.getDBCollection().find(query).setOptions(options);
    }
}
