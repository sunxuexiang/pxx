package com.wanmi.sbc.pay.api.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/8/1 15:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcbPayOrderRecordResponse {

    /**
     * ID
     */
    private Long id;

    /**
     * 建行商家编码
     */
    private String mktMrchtId;

    /**
     * 业务订单ID
     */
    private String businessId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 交易金额
     */
    private BigDecimal txnAmt;

    /**
     * 主订单ID
     */
    private String mainOrderNo;

    /**
     * 惠市宝订单ID
     */
    private String primOrdrNo;

    /**
     * 商品订单号
     */
    private String cmdtyOrderNo;

    /**
     * 惠市宝支付流水
     */
    private String pyTrnNo;

    /**
     * 惠市宝子订单ID
     */
    private String subOrdrId;

    /**
     * 比例
     */
    private BigDecimal ratio;

    /**
     * 佣金
     */
    private BigDecimal commission;

    /**
     * 原来总金额
     */
    private BigDecimal totalAmt;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否是佣金
     */
    private BoolFlag commissionFlag;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;


    /**
     * 0初始 1未清算  2已清算  3无需清算  4清算异常 6清算中 b待清算
     */
    private String clrgStcd;

    /**
     * 清算日期
     */
    private String clrgDt;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 运费佣金
     */
    private BigDecimal freightCommission;

    /**
     * 运费加收
     */
    private BigDecimal extra;

    /**
     * 运费加收佣金
     */
    private BigDecimal extraCommission;
}
