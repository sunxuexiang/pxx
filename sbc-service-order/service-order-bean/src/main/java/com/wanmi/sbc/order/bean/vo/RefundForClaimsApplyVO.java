package com.wanmi.sbc.order.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 理赔退款申请VO
 *
 * @author chenchang
 * @since 2023/04/20 17:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class RefundForClaimsApplyVO {
    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "理赔申请单号")
    private String applyNo;

    @ApiModelProperty(value = "用户账号")
    private String customerAccount;

    @ApiModelProperty(value = "联系方式")
    private String contactPhone;

    @ApiModelProperty(value = "充值时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime rechargeTime;

    @ApiModelProperty(value = "充值人ID")
    private String operatorId;

    @ApiModelProperty(value = "充值人")
    private String operatorName;

    @ApiModelProperty(value = "充值类型")
    private String rechargeTypeText;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeBalance;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "退单号")
    private String returnOrderNo;

    @ApiModelProperty(value = "充值类型ID")
    private Integer chaimApllyType;
}