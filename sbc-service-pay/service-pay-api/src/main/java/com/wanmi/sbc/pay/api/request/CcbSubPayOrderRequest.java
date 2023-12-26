package com.wanmi.sbc.pay.api.request;

import com.wanmi.sbc.pay.bean.enums.CcbSubOrderType;
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
 * @create 2023/6/16 14:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CcbSubPayOrderRequest implements Serializable {

    private static final long serialVersionUID = 8694084559431338062L;

    /**
     * 商家编号
     */
    private String mktMrchtId;

    /**
     * 商品订单号
     */
    private String cmdtyOrderNo;

    /**
     * 订单金额 订单商品总金额，即应付金额(商品金额)
     */
    private BigDecimal orderAmount;

    /**
     * 交易金额 消费者实付金额(结算金额)
     */
    private BigDecimal txnAmt;

    /**
     *  附加项总金额
     */
    private BigDecimal apdAmt;

    /**
     * 分账规则ID
     */
    private String clrgRuleId;

    private List<CcbSubOrderParRequest> parList;

    /**
     * 订单Id
     */
    private String tid;

    /**
     * 多少天之后开始分账
     */
    private Long days;

    /**
     * 比例
     */
    private BigDecimal ratio;

    /**
     * 是否是佣金
     */
    private CcbSubOrderType commissionFlag;

    /**
     * 佣金
     */
    private BigDecimal commission;

    /**
     * 原来总金额
     */
    private BigDecimal totalAmt;

    /**
     * 配送到店运费
     */
    private BigDecimal freight;

    /**
     * 配送到店运费佣金
     */
    private BigDecimal freightCommission;

    /**
     * 加收
     */
    private BigDecimal extra;

    /**
     * 加收佣金
     */
    private BigDecimal extraCommission;


}
