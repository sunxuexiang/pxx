package com.wanmi.sbc.order.api.request.purchase;

import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class BatchAddCartGoodsDTO {

    @ApiModelProperty(value = "订单ID")
    private String orderId;

    @ApiModelProperty(value = "订单的类型")
    private SaleType saleType;
}
