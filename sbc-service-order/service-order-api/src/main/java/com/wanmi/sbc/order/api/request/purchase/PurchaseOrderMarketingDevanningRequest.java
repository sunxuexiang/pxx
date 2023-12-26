package com.wanmi.sbc.order.api.request.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class PurchaseOrderMarketingDevanningRequest {

    @ApiModelProperty(value = "订单金额")
    private BigDecimal totalPrice;

    /**
     * 不是箱商品的最小单位数量
     */
    @ApiModelProperty(value = "订单数量")
    private Long goodsTotalNum;

}
