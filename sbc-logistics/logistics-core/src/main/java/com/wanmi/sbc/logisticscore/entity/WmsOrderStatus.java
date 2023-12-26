package com.wanmi.sbc.logisticscore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WMS订单状态
 * @author lm
 * @date 2022/11/08 16:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WmsOrderStatus {


    /**
     * 订单ID
     */
    private String tid;

    /**
     * 支付状态，N:未付款，Y:已付款
     */
    private String payStatus;

    /**
     * 订单状态：
     *     1 -> 已发货
     *     2 -> 已取消
     *     0 -> 创建状态
     *     3 -> 拣货中
     */
    private Integer orderStatus;
}
