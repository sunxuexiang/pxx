package com.wanmi.sbc.pay.model.root;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 建行对账单退款
 * @author hudong
 * 2023-09-04 15:42
 */
@Data
@Entity
@Table(name = "ccb_statement_refund")
@Accessors(chain = true)
public class CcbStatementRefund implements Serializable {

    private static final long serialVersionUID = -6190599577101421436L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 订单号
     */
    @Column(name = "py_ordr_no")
    private String pyOrdrNo;

    /**
     * 扣减金额
     */
    @Column(name = "tfr_amt")
    private BigDecimal tfrAmt;

    /**
     * 扣减账号
     */
    @Column(name = "pyr_acc_no")
    private String pyrAccNo;

    /**
     * 项目编号
     */
    @Column(name = "prj_id")
    private String prjId;

    /**
     * 项目名称
     */
    @Column(name = "prj_nm")
    private String prjNm;

    /**
     * 扣减状态代码
     */
    @Column(name = "tfr_stcd")
    private String tfrStcd;

    /**
     * 扣减日期
     */
    @Column(name = "tfr_dt")
    private Date tfrDt;

    /**
     * 主订单编号
     */
    @Column(name = "main_ordr_no")
    private String mainOrdrNo;

}
