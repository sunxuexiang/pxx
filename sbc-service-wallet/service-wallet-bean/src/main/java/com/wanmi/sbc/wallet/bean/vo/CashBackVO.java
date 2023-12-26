package com.wanmi.sbc.wallet.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CashBackVO implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 会员id
     */
    private String customerId;

    /**
     * 会员登录账号|手机号
     */
    private String customerAccount;

    /**
     * 会员名称
     */
    private String customerName;

    /**
     * 订单类型 0囤货订单 1 提货订单
     */
    private Long orderType;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单金额
     */
    private BigDecimal orderPrice;

    /**
     * 应返金额
     */
    private BigDecimal returnPrice;

    /**
     * 实返金额
     */
    private BigDecimal realPrice;

    /**
     * 提货时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime pickTime;

    /**
     * 收货时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime acceptTime;

    /**
     * 0 待打款 1 已完成
     */
    private Long returnStatus;

    /**
     * 业务员
     */
    private String operatorName;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
}
