package com.wanmi.sbc.pay.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 建行支付记录表
 */
@Data
@Entity
@Table(name = "ccb_pay_record")
public class CcbPayRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "record_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    /**
     * 业务订单编号
     */
    @Column(name = "business_id")
    private String businessId;

    /**
     * 建行市场编码
     */
    @Column(name = "mkt_id")
    private String mktId;

    /**
     * 主订单编码
     */
    @Column(name = "main_ordr_no")
    private String mainOrdrNo;

    /**
     * 建行主订单编号
     */
    @Column(name = "prim_ordr_no")
    private String primOrdrNo;

    /**
     * 建行支付流水
     */
    @Column(name = "py_trn_no")
    private String pyTrnNo;

    /**
     * 建行支付类型
     */
    @Column(name = "pymd_cd")
    private String pymdCd;

    /**
     * 订单金额
     */
    @Column(name = "ordr_tamt")
    private BigDecimal ordrTamt;

    /**
     * 实付金额
     */
    @Column(name = "txn_tamt")
    private BigDecimal txnTamt;

    /**
     * 清算日期 分账日期 = T+1
     */
    @Column(name = "clrg_dt")
    private String clrgDt;

    /**
     * 状态
     */
    @Column(name = "status")
    private Integer status;

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
     * 00-分账处理成功,01-分账处理失败
     */
    @Column(name = "sub_acc_stcd")
    private String subAccStcd;

    @Column(name = "pay_type")
    private Integer payType;

    /**
     * 建行对公支付付款凭证
     */
    @Column(name = "ccb_pay_img")
    private String ccbPayImg;

}
