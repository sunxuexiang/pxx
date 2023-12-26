package com.wanmi.sbc.account.api.request.wallet;

import com.wanmi.sbc.marketing.bean.vo.CheckGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletTradePriceRequest {

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    /**
     * 是否使用余额
     */
    @ApiModelProperty(value = "是否使用余额")
    @NotNull
    private Boolean useWallet;

    /**
     * 商品是否含运费
     */
    @ApiModelProperty(value = "商品是否含运费")
    @NotNull
    private Boolean isDeliveryPrice;

    /**
     * 商品总价
     */
    @ApiModelProperty(value = "商品总价")
    private BigDecimal totalPrice;

    /**
     * 均摊商品价格
     */
    @ApiModelProperty(value = "商品价格")
    List<CheckGoodsInfoVO> checkGoodsInfos;

}
