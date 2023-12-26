package com.wanmi.sbc.pay.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/17 15:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbRefundRequest implements Serializable {

    private static final long serialVersionUID = -1038027550262337059L;

    /**
     * 支付单号
     */
    private String payTrnNo;

    /**
     * 退单号
     */
    private String custRfndTrcno;

    /**
     * 退单金额
     */
    private BigDecimal rfndAmt;

    /**
     * 退单子订单列表
     */
    private List<CcbRefundSubRequest> subOrderList;

    /**
     * 参与方列表
     */
    private List<CcbRefundParRequest> parOrderList;

    /**
     * 订单ID
     */
    private String tid;

    /**
     * 是否退运费
     */
    private Boolean refundFreight;

    /**
     * 退运费
     */
    private BigDecimal freightPrice;

    /**
     * 退单号
     */
    private String rid;


    /**
     * 运费加收金额
     */
    private BigDecimal extraPrice;

}
