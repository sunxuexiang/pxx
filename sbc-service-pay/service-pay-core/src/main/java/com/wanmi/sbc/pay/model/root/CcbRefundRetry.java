package com.wanmi.sbc.pay.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ccb_refund_retry")
public class CcbRefundRetry implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 退单号
     */
    @Column(name = "rid")
    private String rid;

    /**
     * 订单号
     */
    @Column(name = "tid")
    private String tid;

    /**
     * 客户方退款流水号
     */
    @Column(name = "cust_rfnd_trcno")
    private String custRfndTrcno;

    /**
     * 支付流水号
     */
    @Column(name = "py_trn_no")
    private String pyTrnNo;

    /**
     * 退款金额
     */
    @Column(name = "rfnd_amt")
    private BigDecimal rfndAmt;

    /**
     * 请求
     */
    @Column(name = "request")
    private String request;

    /**
     * 响应
     */
    @Column(name = "response")
    private String response;

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

    /**
     * 重试次数
     */
    @Column(name = "retry_count")
    private Integer retryCount;

    /**
     * 退款响应状态
     */
    @Column(name = "refund_rsp_st")
    private String refundRspSt;

    /**
     * 是否退运费
     */
    @Column(name = "refund_freight")
    private Boolean refundFreight;

    /**
     * 运费
     */
    @Column(name = "freight_price")
    private BigDecimal freightPrice;

    /**
     * 运费加收
     */
    @Column(name = "extra_price")
    private BigDecimal extraPrice;

}
