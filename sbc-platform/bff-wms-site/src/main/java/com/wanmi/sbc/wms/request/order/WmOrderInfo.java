package com.wanmi.sbc.wms.request.order;

import com.wanmi.sbc.wms.request.WMSPushOrderRequest;
import lombok.Data;

import java.util.List;

/**
 * @author baijianzhong
 * @ClassName WmOrderInfo
 * @Date 2020-07-17 10:19
 * @Description TODO
 **/
@Data
public class WmOrderInfo {

    public List<WMSPushOrderRequest> orderinfo;

}
