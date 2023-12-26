package com.wanmi.sbc.wms.request.back;

import com.wanmi.sbc.wms.request.WMSChargeBackRequest;
import lombok.Data;

import java.util.List;

/**
 * @author baijianzhong
 * @ClassName WmOrderInfo
 * @Date 2020-07-17 10:19
 * @Description TODO
 **/
@Data
public class WmReturnInfo {

    private List<WMSChargeBackRequest> orderinfo;

}
