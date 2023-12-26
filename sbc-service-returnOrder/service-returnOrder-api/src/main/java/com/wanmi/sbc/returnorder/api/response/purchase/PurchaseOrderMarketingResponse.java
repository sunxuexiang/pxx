package com.wanmi.sbc.returnorder.api.response.purchase;

import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/5/6
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderMarketingResponse implements Serializable {

    /**
     * 订单总额
     */
    @ApiModelProperty(value = "订单总额")
    private BigDecimal totalPrice ;

    /**
     * 满订单优惠
     */
    @ApiModelProperty(value = "满订单优惠")
    private BigDecimal discountsTotalOrderPrice;

    /**
     * 满订单优惠
     */
    @ApiModelProperty(value = "满订单类型(7:满订单减,8：满订单折)")
    private MarketingSubType subType;

}
