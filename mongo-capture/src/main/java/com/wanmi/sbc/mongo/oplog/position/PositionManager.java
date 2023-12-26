package com.wanmi.sbc.mongo.oplog.position;

import com.wanmi.sbc.mongo.oplog.comm.MongoCaptureLifeCycle;
import com.wanmi.sbc.mongo.oplog.data.Position;

/**
 * @author zhanggaolei
 */
public interface PositionManager extends MongoCaptureLifeCycle {

    /**
     * 获取 cursor 游标
     */
    Position getCursor() ;

    /**
     * 更新 cursor 游标
     */
    void updateCursor(Position position);


    /**
     * 获得该client最新的一个位置
     */
    void removeCursor();

}
