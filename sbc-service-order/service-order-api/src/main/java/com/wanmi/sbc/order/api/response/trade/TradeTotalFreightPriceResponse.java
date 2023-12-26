package com.wanmi.sbc.order.api.response.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 各店铺运费
 */
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class TradeTotalFreightPriceResponse implements Serializable {

    /**
     * 配送费用，可以从TradePriceInfo获取
     */
    @ApiModelProperty(value = "配送费用")
    private BigDecimal deliveryPrice;

}
