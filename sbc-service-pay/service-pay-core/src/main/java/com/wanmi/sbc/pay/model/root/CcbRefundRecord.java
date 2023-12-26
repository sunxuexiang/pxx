package com.wanmi.sbc.pay.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 惠市宝退款记录
 */
@Data
@Entity
@Table(name = "ccb_refund_record")
public class CcbRefundRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 退单ID
     */
    @Column(name = "rid")
    private String rid;

    /**
     * 订单ID
     */
    @Column(name = "tid")
    private String tid;

    /**
     * OD单号
     */
    @Column(name = "main_order_no")
    private String mainOrderNo;

    /**
     * 支付流水号
     */
    @Column(name = "py_trn_no")
    private String pyTrnNo;

    /**
     * 状态（1：成功，2：退款中，3：失败）
     */
    @Column(name = "refund_status")
    private Integer refundStatus;

    /**
     * 退款金额
     */
    @Column(name = "refund_price")
    private BigDecimal refundPrice;

    /**
     * 退运费金额
     */
    @Column(name = "refund_freight")
    private BigDecimal refundFreight;

    /**
     * 商家金额
     */
    @Column(name = "mkt_price")
    private BigDecimal mktPrice;

    /**
     * 运费金额
     */
    @Column(name = "freight_price")
    private BigDecimal freightPrice;

    /**
     * 佣金金额
     */
    @Column(name = "commission_price")
    private BigDecimal commissionPrice;

    /**
     * 运费佣金
     */
    @Column(name = "freight_commission_price")
    private BigDecimal freightCommissionPrice;

    /**
     * 总加收金额
     */
    @Column(name = "total_extra")
    private BigDecimal totalExtra;

    /**
     * 运费加收承运商金额
     */
    @Column(name = "extra_price")
    private BigDecimal extraPrice;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

}
