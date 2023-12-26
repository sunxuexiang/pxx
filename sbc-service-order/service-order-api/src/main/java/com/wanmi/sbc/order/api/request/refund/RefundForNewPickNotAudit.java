package com.wanmi.sbc.order.api.request.refund;

import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RefundForNewPickNotAudit {
    TradeVO tradeVO;
    ReturnOrderVO returnOrder;
}
