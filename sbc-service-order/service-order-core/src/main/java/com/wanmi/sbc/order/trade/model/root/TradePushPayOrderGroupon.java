package com.wanmi.sbc.order.trade.model.root;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 支付单延时推金蝶
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class TradePushPayOrderGroupon implements Serializable {
    private static final long serialVersionUID = -7921689052799090622L;

    /**
     * 销售订单id
     */
    private String orderCode;

    /**
     * 支付订单id
     */
    private String payCode;

}
