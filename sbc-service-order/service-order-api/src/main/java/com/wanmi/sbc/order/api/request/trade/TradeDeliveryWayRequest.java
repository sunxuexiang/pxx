package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.order.bean.dto.TradeDeliveryWayDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class TradeDeliveryWayRequest implements Serializable {
    private static final long serialVersionUID = 719026499910276135L;

    @ApiModelProperty(value = "用户id", name = "customerId")
    private String customerId;

    @ApiModelProperty(value = "收货地址id", name = "addressId")
    private String addressId;

    @ApiModelProperty(value = "商家商品信息", name = "deliveryWayDTOS")
    List<TradeDeliveryWayDTO> deliveryWayDTOS;
}
