package com.wanmi.sbc.mongo.oplog.data;

import com.wanmi.sbc.mongo.oplog.utils.AddressUtils;
import lombok.Data;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: zhanggaolei
 * \* @date: 2019-12-18
 * \* @time: 14:15
 * \* To change this template use File | Settings | File Templates.
 * \* @description:
 * \
 */
@Data
public class RunningData {

/*    private short   clientId;*/
    private String  address;
    private boolean active = true;

    public RunningData(){
        this(AddressUtils.getHostIp());
    }
    public RunningData(String address){
        this.address = address;
    }
}
