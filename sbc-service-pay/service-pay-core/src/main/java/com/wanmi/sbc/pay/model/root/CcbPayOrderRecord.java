package com.wanmi.sbc.pay.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.pay.bean.enums.CcbSubOrderType;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 惠市宝子单详情
 */
@Data
@Entity
@Table(name = "ccb_pay_order_record")
public class CcbPayOrderRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 建行商家编码
     */
    @Column(name = "mkt_mrcht_id")
    private String mktMrchtId;

    /**
     * 业务订单ID
     */
    @Column(name = "business_id")
    private String businessId;

    /**
     * 订单金额
     */
    @Column(name = "order_amount")
    private BigDecimal orderAmount;

    /**
     * 交易金额
     */
    @Column(name = "txn_amt")
    private BigDecimal txnAmt;

    /**
     * 主订单ID
     */
    @Column(name = "main_order_no")
    private String mainOrderNo;

    /**
     * 惠市宝订单ID
     */
    @Column(name = "prim_ordr_no")
    private String primOrdrNo;

    /**
     * 商品订单号
     */
    @Column(name = "cmdty_order_no")
    private String cmdtyOrderNo;

    /**
     * 惠市宝支付流水
     */
    @Column(name = "py_trn_no")
    private String pyTrnNo;

    /**
     * 惠市宝子订单ID
     */
    @Column(name = "sub_ordr_id")
    private String subOrdrId;

    /**
     * 比例
     */
    @Column(name = "ratio")
    private BigDecimal ratio;

    /**
     * 佣金
     */
    @Column(name = "commission")
    private BigDecimal commission;

    /**
     * 原来总金额
     */
    @Column(name = "total_amt")
    private BigDecimal totalAmt;

    /**
     * 状态
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 是否是佣金
     */
    @Column(name = "commission_flag")
    @Enumerated
    private CcbSubOrderType commissionFlag;

    /**
     * 退款金额
     */
    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;


    /**
     * 0初始 1未清算  2已清算  3无需清算  4清算异常 6清算中 b待清算
     */
    @Column(name = "clrg_stcd")
    private String clrgStcd;

    /**
     * 清算日期
     */
    @Column(name = "clrg_dt")
    private String clrgDt;

    /**
     * 运费
     */
    @Column(name = "freight")
    private BigDecimal freight;

    /**
     * 运费
     */
    @Column(name = "freight_commission")
    private BigDecimal freightCommission;

    /**
     * 运费加收
     */
    @Column(name = "extra")
    private BigDecimal extra;

    /**
     * 运费加收佣金
     */
    @Column(name = "extra_commission")
    private BigDecimal extraCommission;
}
