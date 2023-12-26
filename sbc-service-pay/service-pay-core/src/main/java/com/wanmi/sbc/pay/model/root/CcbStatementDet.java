package com.wanmi.sbc.pay.model.root;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 建行分账明细
 * @author hudong
 * 2023-09-19 15:42
 */
@Data
@Entity
@Table(name = "ccb_statement_det")
@Accessors(chain = true)
public class CcbStatementDet implements Serializable {

    private static final long serialVersionUID = -6190599577201421536L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 序号
     */
    @Column(name = "sn")
    private String sn;

    /**
     * 支付流水号
     */
    @Column(name = "py_ordr_no")
    private String pyOrdrNo;

    /**
     * 收款账号
     */
    @Column(name = "rcvpymt_acc_no")
    private String rcvpymtAccNo;

    /**
     * 收款方商家编号
     */
    @Column(name = "rcvprt_mkt_mrch_id")
    private String rcvprtMktMrchId;

    /**
     * 收款方商家名称
     */
    @Column(name = "rcvprt_mkt_mrch_nm")
    private String rcvprtMktMrchNm;

    /**
     * 分账金额
     */
    @Column(name = "clrg_amt")
    private BigDecimal clrgAmt;

    /**
     * 分账状态代码 2-分账成功  4-分账异常
     */
    @Column(name = "clrg_stcd")
    private String clrgStcd;

    /**
     * 分账日期
     */
    @Column(name = "clrg_dt")
    private Date clrgDt;

    /**
     * 分摊手续费
     */
    @Column(name = "hdcg_amt")
    private BigDecimal hdcgAmt;

    /**
     * 子订单编号
     */
    @Column(name = "sub_ordr_no")
    private String subOrdrNo;

    /**
     * 原始分账金额
     */
    @Column(name = "shld_subacc_amt")
    private BigDecimal shldSubaccAmt;

    /**
     * 商家自定义名称
     */
    @Column(name = "udf_id")
    private String udfId;


    /**
     * 主订单编号
     */
    @Column(name = "main_ordr_no")
    private String mainOrdrNo;

}
