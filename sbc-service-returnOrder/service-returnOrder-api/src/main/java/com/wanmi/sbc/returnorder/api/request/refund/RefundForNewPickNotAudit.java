package com.wanmi.sbc.returnorder.api.request.refund;

import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
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
