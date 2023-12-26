package com.wanmi.sbc.mongo.oplog.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.bson.types.BSONTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* Date: 2019-12-13
 * \* Time: 17:45
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
@Data
public class Position {

    private BSONTimestamp position;
    private String model;
    private String clusterName;
    private LocalDateTime dateTime;

    public Position(){

    }
    public Position(String model,String clusterName){
        this.model = model;
        this.clusterName = clusterName;
    }


    /**
     * 为了解决BSONTimestamp空构造函数默认赋值了造成json反序列化的时候position字段无法解析过来
     * 的问题，新增这个方法
     * @param json
     * @return
     */
    public static Position jsonToBean(String json){
        Position position = JSONObject.parseObject(json,Position.class);
        JSONObject jsonObject = JSONObject.parseObject(json).getJSONObject("position");
        if(jsonObject!=null) {
            BSONTimestamp bsonTimestamp = new BSONTimestamp(jsonObject.getIntValue("time"), jsonObject.getIntValue("inc"));
            position.setPosition(bsonTimestamp);
        }
        return position;
    }

    /**
     * 为了解决BSONTimestamp空构造函数默认赋值了造成json反序列化的时候position字段无法解析过来
     * 的问题，新增这个方法
     * @param jsonBytes
     * @return
     */
    public static Position jsonToBean(byte[] jsonBytes){
        Position position = JSONObject.parseObject(jsonBytes,Position.class);
        String json = new String(jsonBytes);
        JSONObject jsonObject = JSONObject.parseObject(json).getJSONObject("position");
        if(jsonObject!=null) {
            BSONTimestamp bsonTimestamp = new BSONTimestamp(jsonObject.getIntValue("time"), jsonObject.getIntValue("inc"));
            position.setPosition(bsonTimestamp);
        }
        return position;
    }

}
