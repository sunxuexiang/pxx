package com.wanmi.sbc.returnorder.trade.reponse;

import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 各店铺运费
 */
@Data
public class TradeFreightResponse {
    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 商家名称
     */
    private String storeName;

    /**
     * 配送费用，可以从TradePriceInfo获取
     */
    private BigDecimal deliveryPrice;

    /**
     * 配送优惠费用，可以从TradePriceInfo获取
     */
    @ApiModelProperty(value = "配送优惠费用")
    private BigDecimal deliveryCouponPrice;

    /**
     * 运费类型 0-批发，1零售
     */
    @ApiModelProperty(value = "运费类型 0-批发，1-零售")
    private SaleType saleType = SaleType.WHOLESALE;

    /**
     * 计算运费规则描述
     */
    private String freightRuleDesc;

}
