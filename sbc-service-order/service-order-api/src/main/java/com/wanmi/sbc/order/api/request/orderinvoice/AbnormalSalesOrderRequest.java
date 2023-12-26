package com.wanmi.sbc.order.api.request.orderinvoice;

import com.wanmi.sbc.order.bean.enums.KingdeeAbnormalOrderEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 处理订单推金蝶异常
 *
 * @author yitang
 * @version 1.0
 */
public class AbnormalSalesOrderRequest implements Serializable {
    private static final long serialVersionUID = -6560290616817665975L;

    /**
     * 订单类型
     */
    private KingdeeAbnormalOrderEnum orderType;

    /**
     * 截止时间
     */
    private LocalDateTime byTime;

}
