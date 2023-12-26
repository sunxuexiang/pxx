package com.wanmi.sbc.order.api.request.manualrefund;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author chenchang
 * @since 2023/04/20 17:23
 */
@ApiModel
@Data
public class RefundForClaimsApplyPageRequest extends BaseQueryRequest {
    @ApiModelProperty(value = "用户账号")
    private String customerAccount;

    @ApiModelProperty(value = "充值账号")
    private String operatorName;

    @ApiModelProperty(value = "充值金额范围（从）")
    private BigDecimal rechargeBalanceFrom;

    @ApiModelProperty(value = "充值金额范围（到）")
    private BigDecimal rechargeBalanceTo;

    @ApiModelProperty(value = "充值时间范围（从）")
    private String rechargeTimeFrom;

    @ApiModelProperty(value = "充值时间范围（到）")
    private String rechargeTimeTo;

    @ApiModelProperty(value = "鲸币充值类型")
    private Integer chaimApllyType;

}
