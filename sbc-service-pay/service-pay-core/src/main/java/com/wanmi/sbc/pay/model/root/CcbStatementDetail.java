package com.wanmi.sbc.pay.model.root;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 建行对账单明细
 * @author hudong
 * 2023-09-04 15:42
 */
@Data
@Entity
@Table(name = "ccb_statement_detail")
@Accessors(chain = true)
public class CcbStatementDetail implements Serializable {

    private static final long serialVersionUID = -6190599577201421436L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
     * 交易完成时间
     */
    @Column(name = "txn_dt")
    private Date txnDt;

    /**
     * 支付流水号
     */
    @Column(name = "ordr_no")
    private String ordrNo;

    /**
     * 订单类型代码 1支付 2退款
     */
    @Column(name = "ordr_tpcd")
    private String ordrTpcd;

    /**
     * 交易金额
     */
    @Column(name = "txn_amt")
    private BigDecimal txnAmt;

    /**
     * 手续费
     */
    @Column(name = "hd_cg")
    private BigDecimal hdCg;

    /**
     * 收款状态代码 00-初始，01-对平，03-交易金额不一致，04-平台多，05-收单多
     */
    @Column(name = "rcncl_rslt_stcd")
    private String rcnclRsltStcd;

    /**
     * 交易类型
     */
    @Column(name = "txn_tp_dsc")
    private String txnTpDsc;

    /**
     * 发卡行
     */
    @Column(name = "lssubnk_dsc")
    private String lssubnkDsc;

    /**
     * 卡种
     */
    @Column(name = "py_crdtp_dsc")
    private String pyCrdtpDsc;

    /**
     * 卡号
     */
    @Column(name = "pyr_accNo")
    private String pyrAccNo;

    /**
     * 交易时间
     */
    @Column(name = "txn_tm")
    private Date txnTm;

    /**
     * 主订单编号
     */
    @Column(name = "main_ordr_no")
    private String mainOrdrNo;

    /**
     * 客户方退款流水号
     */
    @Column(name = "cust_rfnd_trcno")
    private String custRfndTrcno;

    /**
     * 付款人账号匹配结果
     */
    @Column(name = "match_payer_acct_rslt")
    private String matchPayerAcctRslt;

    /**
     * 付款人户名匹配结果
     */
    @Column(name = "match_payer_name_rslt")
    private String matchPayerNameRslt;

}
