package com.wanmi.sbc.mongo.oplog.tunnel;

import com.wanmi.sbc.mongo.oplog.utils.SpringContextHelper;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-08-14 17:47
 */
public class TunnelParser {

    public static TunnelLauncher parse(String tunnel){
        switch (tunnel){
            case "kafka": return SpringContextHelper.getBean("kafkaTunnelLauncher");
            default: return null;
        }
    }


}
