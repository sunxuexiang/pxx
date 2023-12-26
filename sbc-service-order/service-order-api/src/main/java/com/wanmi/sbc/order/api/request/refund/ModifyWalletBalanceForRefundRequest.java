package com.wanmi.sbc.order.api.request.refund;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ModifyWalletBalanceForRefundRequest implements Serializable {

    private static final long serialVersionUID = -1242317779343601050L;

    @ApiModelProperty(value = "退单id")
    private String rid;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "买家id")
    private String buyerId;

    @ApiModelProperty(value = "会员账号")
    private String customerAccount;

    @ApiModelProperty(value = "交易备注")
    private String tradeRemark;

    @ApiModelProperty(value = "备注")
    private String remark;
}