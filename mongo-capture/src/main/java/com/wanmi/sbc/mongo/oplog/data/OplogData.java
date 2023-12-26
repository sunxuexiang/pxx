package com.wanmi.sbc.mongo.oplog.data;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

 /**
   * @Description: MonogDB oplog映射数据
   * @Author: ZhangLingKe
   * @CreateDate: 2019/8/14 17:35
   */
@Data
public class OplogData implements Serializable {

    /**
     * 数据操作类型
     *      oplog字段：op
     */
    private RowBehavior type;

    /**
     * 操作的数据库
     *      oplog字段：ns前半部分
     */
    private String database;

    /**
     * 操作的集合名
     *      oplog字段：ns后半部分
     */
    private String collection;

    /**
     * 操作的内容
     *      oplog字段：o
     */
    private String data;

    /**
     * 更新操作时的where条件
     *      oplog字段：o2
     */
    private String condition;

    /**
    * mongoDB 的_id字段，如果是objectId取的是toString
    *
    */
    @JSONField(serialize = false)
    private transient String _id;

    public OplogData(){
    }


    public OplogData(RowBehavior type, String database, String collection, String data, String condition,String _id){
        this.type = type;
        this.database = database;
        this.collection = collection;
        this.data = data;
        this.condition = condition;
        this._id = _id;
    }
}
