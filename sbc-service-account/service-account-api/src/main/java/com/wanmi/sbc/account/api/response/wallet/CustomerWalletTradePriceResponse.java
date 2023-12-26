package com.wanmi.sbc.account.api.response.wallet;

import com.wanmi.sbc.marketing.bean.vo.CheckGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletTradePriceResponse {

    /**
     * 商品总价
     */
    @ApiModelProperty(value = "计算完余额均摊价的商品总价")
    private BigDecimal totalPrice;

    /**
     * 余额扣减总价
     */
    @ApiModelProperty(value = "余额扣减总价")
    private BigDecimal walletTotalPrice;

    /**
     * 商品价格
     */
    @ApiModelProperty(value = "商品价格")
    List<CheckGoodsInfoVO> checkGoodsInfos;
}
