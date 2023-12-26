package com.wanmi.sbc.returnorder.claims.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.returnorder.bean.enums.ClaimsApplyType;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author chenchang
 * @since 2023/04/21 9:48
 */
@Data
@Entity
@Table(name = "claims_apply")
public class ClaimsApply {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;

    /**
     * 理赔申请单号
     */
    @Basic
    @Column(name = "apply_no")
    private String applyNo;

    /**
     * 用户账号
     */
    @Basic
    @Column(name = "customer_account")
    private String customerAccount;

    /**
     * 充值金额
     */
    @Basic
    @Column(name = "recharge_balance")
    private BigDecimal rechargeBalance;

    /**
     * 订单号
     */
    @Basic
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 退单号
     */
    @Basic
    @Column(name = "return_order_no")
    private String returnOrderNo;

    /**
     * 备注
     */
    @Basic
    @Column(name = "remark")
    private String remark;


    /**
     * 单据状态：0 审核中 1 已审核
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 申请人
     */
    @Basic
    @Column(name = "apply_user_id")
    private String applyUserId;

    /**
     * 申请人姓名
     */
    @Basic
    @Column(name = "apply_user_name")
    private String applyUserName;

    /**
     * 审核人
     */
    @Basic
    @Column(name = "approve_user_id")
    private String approveUserId;

    /**
     * 审核人姓名
     */
    @Column(name = "approve_user_name")
    private String approveUserName;

    /**
     * 申请时间
     */
    @Basic
    @Column(name = "apply_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;

    /**
     * 审核时间
     */
    @Basic
    @Column(name = "approve_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime approveTime;


    /**
     * 类型1手动充值；2手动扣除
     */
    @Basic
    @Column(name = "apply_type")
    @Enumerated
    private ClaimsApplyType applyType;
}
