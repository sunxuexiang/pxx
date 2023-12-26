package com.wanmi.sbc.account.cashBack.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cash_back")
public class CashBack {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    /**
     * 会员ID
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 会员登录账号|手机号
     */
    @Column(name = "customer_account")
    private String customerAccount;

    /**
     * 会员名称
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * 订单类型 0囤货订单 1 提货订单
     */
    @Column(name = "order_type")
    private Long orderType;

    /**
     * 订单编号
     */
    @Column(name = "order_code")
    private String orderCode;

    /**
     * 订单金额
     */
    @Column(name = "order_price")
    private BigDecimal orderPrice;

    /**
     * 应返金额
     */
    @Column(name = "return_price")
    private BigDecimal returnPrice;

    /**
     * 实返金额
     */
    @Column(name = "real_price")
    private BigDecimal realPrice;

    /**
     * 提货时间
     */
    @Column(name = "pick_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime pickTime;

    /**
     * 收货时间
     */
    @Column(name = "accept_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime acceptTime;

    /**
     * 0 待打款 1 已完成
     */
    @Column(name = "return_status")
    private Long returnStatus;

    /**
     * 业务员
     */
    @Column(name = "operator_name")
    private String operatorName;

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
}
