package com.wanmi.sbc.wallet.wallet.model.root;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets_form")
public class TicketsFormQuery implements Serializable {
    private static final long serialVersionUID = 2321329611892557532L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    private Long formId;

    /**
     * 钱包id
     */
    @Column(name = "wallet_id")
    private Long walletId;

    /**
     * 虚拟商品id
     */
    @Column(name = "virtual_goods_id")
    private Integer virtualGoodsId;

    /**
     * 银行名称
     */
    @Column(name = "bank_name")
    private String bankName;

    /**
     * 支行
     */
    @Column(name = "bank_branch")
    private String bankBranch;

    /**
     * 银行卡号
     */
    @Column(name = "bank_code")
    private String bankCode;

    /**
     * 工单类型
     */
    @Column(name = "apply_type")
    private Integer applyType;

    /**
     * 申请金额
     */
    @Column(name = "apply_price")
    private BigDecimal applyPrice;

    /**
     * 申请时间
     */
    @Column(name = "apply_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;

    /**
     * 充值申请单状态【1待审核，2充值成功，3充值失败】
     */
    @Column(name = "recharge_status")
    private Integer rechargeStatus;

    /**
     * 提现申请单状态【1待审核，2已审核，3已打款，4已拒绝】
     */
    @Column(name = "extract_status")
    private Integer extractStatus;

    /**
     * 到账金额
     */
    @Column(name = "arrival_price")
    private BigDecimal arrivalPrice;

    /**
     * 审核时间
     */
    @Column(name = "audit_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime auditTime;

    /**
     * 审核人
     */
    @Column(name = "audit_admin")
    private String auditAdmin;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 交易单号
     */
    @Column(name = "record_no")
    private String recordNo;

    /**
     * 转账账户id
     */
    @Column(name = "account_id")
    private Long accountId;

    /**
     * 转账银行卡号
     */
    @Column(name = "bank_no")
    private String bankNo;

    /**
     * 转账时间
     */
    @Column(name = "transfer_date")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime transferDate;

    /**
     * 客户姓名
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * 客户电话
     */
    @Column(name = "customer_phone")
    private String customerPhone;


    /**
     * 会员的详细信息
     */
    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "wallet_id", insertable = false, updatable = false)
    private CustomerWallet customerWallet;

}
