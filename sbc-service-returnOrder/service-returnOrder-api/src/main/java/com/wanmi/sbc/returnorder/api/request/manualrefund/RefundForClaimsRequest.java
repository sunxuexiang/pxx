package com.wanmi.sbc.returnorder.api.request.manualrefund;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.enums.ClaimsApplyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author chenchang
 * @since 2023/04/17 15:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class RefundForClaimsRequest {
    @ApiModelProperty(value = "操作人")
    private Operator operator;

    @ApiModelProperty(value = "用户账号")
    @NotBlank
    private String customerAccount;

    @ApiModelProperty(value = "充值金额")
    @NotNull
    private BigDecimal rechargeBalance;

    @ApiModelProperty(value = "订单号")
    @NotBlank
    private String orderNo;

    @ApiModelProperty(value = "退单号")
    private String returnOrderNo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "类型")
    private ClaimsApplyType claimsApplyType;
}
