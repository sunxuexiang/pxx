package com.wanmi.sbc.mongo.oplog.data;

/**
 * 数据操作行为
 * @author: zhanggaolei
 */
public enum RowBehavior {

    /**
     * 插入操作
     */
    INSERT,

    /**
     * 更新操作
     */
    UPDATE,

    /**
     * 删除操作
     */
    DELETE
}
