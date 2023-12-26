package com.wanmi.sbc.order.api.response.trade;

import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 各店铺运费
 */
@Data
@ApiModel
public class TradeGetFreightResponse implements Serializable {
    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    /**
     * 商家名称
     */
    @ApiModelProperty(value ="商家名称")
    private String storeName;

    /**
     * 配送费用，可以从TradePriceInfo获取
     */
    @ApiModelProperty(value = "配送费用")
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
