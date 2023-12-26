package com.wanmi.sbc.mongo.oplog.tunnel;

import com.wanmi.sbc.mongo.oplog.data.OplogData;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-08-14 17:49
 */
public interface TunnelLauncher {

    /**
     * 发送数据
     * @param oplogData
     */
    void dispatch(OplogData oplogData);

}
