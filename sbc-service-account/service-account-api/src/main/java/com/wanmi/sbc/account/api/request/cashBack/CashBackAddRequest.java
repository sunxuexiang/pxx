package com.wanmi.sbc.account.api.request.cashBack;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel
@Data
public class CashBackAddRequest implements Serializable{

    @ApiModelProperty(value = "会员id")
    private String customerId;
    /**
     * 会员账号
     */
    @ApiModelProperty(value = "会员账号")
    private String customerAccount;

    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称")
    private String customerName;

    /**
     * 订单类型 0囤货订单 1 提货订单
     */
    @ApiModelProperty(value = "订单类型")
    private Long orderType;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderCode;

    @ApiModelProperty(value = "业务员")
    private String operatorName;

    /**
     * 订单金额
     */
    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderPrice;

    /**
     * 应返金额
     */
    @ApiModelProperty(value = "应返金额")
    private BigDecimal returnPrice;

    /**
     * 实返金额
     */
    @ApiModelProperty(value = "实返金额")
    private BigDecimal realPrice;

    /**
     * 提货时间
     */
    @ApiModelProperty(value = "提货时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime pickTime;

    /**
     * 收货时间
     */
    @ApiModelProperty(value = "收货时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime acceptTime;

    /**
     * 0 待打款 1 已完成
     */
    @ApiModelProperty(value = "状态")
    private Long returnStatus;

}
