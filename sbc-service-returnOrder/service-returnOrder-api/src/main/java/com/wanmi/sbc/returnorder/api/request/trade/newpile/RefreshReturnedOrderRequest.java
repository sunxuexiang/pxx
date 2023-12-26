package com.wanmi.sbc.returnorder.api.request.trade.newpile;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class RefreshReturnedOrderRequest {
    //新囤货退单订单id
    List<String> newPileReturnIds;
    //新提货退单id
    List<String> newPickReturnIds;
}
