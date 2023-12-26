package com.wanmi.sbc.pay.model.root;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 建行对账单分账汇总
 * @author hudong
 * 2023-09-23 15:42
 */
@Data
@Entity
@Table(name = "ccb_clrg_summary")
@Accessors(chain = true)
public class CcbClrgSummary implements Serializable {

    private static final long serialVersionUID = -6190599477201421436L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 分账日期
     */
    @Column(name = "clrg_dt")
    private Date clrgDt;

    /**
     * 交易笔数
     */
    @Column(name = "trade_num")
    private Integer tradeNum;

    /**
     * 交易金额
     */
    @Column(name = "trade_amt")
    private BigDecimal tradeAmt;

    /**
     * 退款金额
     */
    @Column(name = "refund_amt")
    private BigDecimal refundAmt;

    /**
     * 平台手续费
     */
    @Column(name = "hdcg_amt")
    private BigDecimal hdcgAmt;

    /**
     * 分账金额
     */
    @Column(name = "clrg_amt")
    private BigDecimal clrgMmt;

    /**
     * 分账账户
     */
    @Column(name = "mkt_mrch_id")
    private String mktMrchId;

    /**
     * 支付流水号
     */
    @Column(name = "py_ordr_no")
    private String pyOrdrNo;

}
