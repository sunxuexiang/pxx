package com.wanmi.sbc.returnorder.bean.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 结算实体
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class OfflineSettlementVO implements Serializable {

    private static final long serialVersionUID = -5076900651196689178L;

    /**
     * 订单支付id
     */
    private String payOrderId;

    /**
     * 实付金额
     */
    private BigDecimal realPay;
}
