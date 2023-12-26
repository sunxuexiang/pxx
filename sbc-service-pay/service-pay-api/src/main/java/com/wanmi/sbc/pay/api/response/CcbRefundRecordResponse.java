package com.wanmi.sbc.pay.api.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/10/16 14:10
 */
@Data
public class CcbRefundRecordResponse {

    /**
     * ID
     */
    private Long id;

    /**
     * 退单ID
     */
    private String rid;

    /**
     * 订单ID
     */
    private String tid;

    /**
     * OD单号
     */
    private String mainOrderNo;

    /**
     * 支付流水号
     */
    private String pyTrnNo;

    /**
     * 状态（1：成功，2：退款中，3：失败）
     */
    private Integer refundStatus;

    /**
     * 退款金额
     */
    private BigDecimal refundPrice;

    /**
     * 商家金额
     */
    private BigDecimal mktPrice;

    /**
     * 运费金额
     */
    private BigDecimal freightPrice;

    /**
     * 佣金金额
     */
    private BigDecimal commissionPrice;

    /**
     * 运费佣金
     */
    private BigDecimal freightCommissionPrice;


    /**
     * 总加收金额
     */
    private BigDecimal totalExtra;

    /**
     * 运费加收承运商金额
     */
    private BigDecimal extraPrice;

}
