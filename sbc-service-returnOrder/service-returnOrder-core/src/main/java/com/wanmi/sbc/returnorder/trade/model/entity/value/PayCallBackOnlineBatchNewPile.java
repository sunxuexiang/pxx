package com.wanmi.sbc.returnorder.trade.model.entity.value;

import com.wanmi.sbc.returnorder.payorder.model.root.PayOrder;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>囤货在线支付批量回调参数</p>
 * Created by of628-wenzhi on 2019-07-25-16:13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayCallBackOnlineBatchNewPile {
    /**
     * 交易单
     */
    private NewPileTrade trade;

    /**
     * 支付单
     */
    private PayOrder payOrderOld;
}
