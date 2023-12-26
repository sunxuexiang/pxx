package com.wanmi.sbc.pay.model.root;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 建行分账规则明细
 */
@Data
@Entity
@Table(name = "ccb_rule_detail")
public class CcbRuleDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "detail_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    /**
     * 主表ID
     */
    @Column(name = "rule_id")
    private Long ruleId;

    /**
     * 顺序号
     */
    @Column(name = "seq_no")
    private Integer seqNo;

    /**
     * 清算方式代码 (4按金额 5按比例)
     */
    @Column(name = "clrg_mtdcd")
    private String clrgMtdcd;

    /**
     * 清算比例 (当【清算方式代码】为按比例时)
     */
    @Column(name = "clrg_pctg")
    private String clrgPctg;

    /**
     * 金额(当【清算方式代码】为按金额时)
     */
    @Column(name = "amt")
    private BigDecimal amt;

}
