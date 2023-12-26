package com.wanmi.sbc.returnorder.api.request.purchase;

import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class BatchAddCartGoodsDTO {

    @ApiModelProperty(value = "订单ID")
    private String orderId;

    @ApiModelProperty(value = "订单的类型")
    private SaleType saleType;
}
