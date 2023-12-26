package com.wanmi.sbc.pay.model.root;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 建行对账单汇总
 * @author hudong
 * 2023-09-04 15:42
 */
@Data
@Entity
@Table(name = "ccb_statement_sum")
@Accessors(chain = true)
public class CcbStatementSum implements Serializable {

    private static final long serialVersionUID = -6190599577201422436L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 分账交易流水号
     */
    @Column(name = "clrg_txnsrlno")
    private String clrgTxnsrlno;

    /**
     * 市场商户编号
     */
    @Column(name = "mkt_mrch_id")
    private String mktMrchId;

    /**
     * 市场商户名称
     */
    @Column(name = "mkt_mrch_nm")
    private String mktMrchNm;

    /**
     * 分账金额
     */
    @Column(name = "to_clrg_amt")
    private BigDecimal toClrgMmt;

    /**
     * 分账失败原因
     */
    @Column(name = "rsp_inf")
    private String rspInf;

    /**
     * 分账状态代码
     */
    @Column(name = "clrg_stcd")
    private String clrgStcd;

    /**
     * 分账日期
     */
    @Column(name = "clrg_dt")
    private Date clrgDt;

    /**
     * 商家自定义名称
     */
    @Column(name = "udf_id")
    private String udfId;



}
