package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.walletorder.bean.vo.TradeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletAwardRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 9094629374388797324L;

    @ApiModelProperty(value = "订单id")
    private String id;

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


    @ApiModelProperty(value = "订单")
    private TradeVO tradeVO;

    @ApiModelProperty(value = "凑箱规格")
    private Long skuNum;

    @ApiModelProperty(value = "省(收货人所在省)")
    private Long provinceId;

    @ApiModelProperty(value = "订单奖励总价")
    private BigDecimal freightCouponPrice;

}
